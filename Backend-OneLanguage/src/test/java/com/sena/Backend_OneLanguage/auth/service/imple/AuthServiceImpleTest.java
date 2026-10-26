package com.sena.Backend_OneLanguage.auth.service.imple;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sena.Backend_OneLanguage.auth.dto.AuthResponseDto;
import com.sena.Backend_OneLanguage.auth.dto.LoginRequestDto;
import com.sena.Backend_OneLanguage.auth.exception.LoginFailureException;
import com.sena.Backend_OneLanguage.auth.mapper.AuthMapper;
import com.sena.Backend_OneLanguage.security.jwt.JwtService;
import com.sena.Backend_OneLanguage.users.dto.UserResponseDto;
import com.sena.Backend_OneLanguage.users.entity.User;
import com.sena.Backend_OneLanguage.users.mapper.UserMapper;
import com.sena.Backend_OneLanguage.users.repository.UserRepository;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceImpleTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthMapper authMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImple authService;

    private User user;
    private LoginRequestDto request;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .idUser(UUID.randomUUID())
                .email("user@example.com")
                .fullName("Test User")
                .passwordHash("encoded-password")
                .failedAttempts(0)
                .userStatus(true)
                .build();
        request = new LoginRequestDto();
        request.setEmail(user.getEmail());
        request.setPassword("wrong-password");
        when(userRepository.findForLoginByEmailAndDeletedAtIsNull(user.getEmail())).thenReturn(Optional.of(user));
    }

    @Test
    void tracksFirstFourFailuresAndReturnsRemainingAttempts() {
        when(passwordEncoder.matches(request.getPassword(), user.getPasswordHash())).thenReturn(false);

        for (int attempt = 1; attempt <= 4; attempt++) {
            LoginFailureException exception = assertThrows(
                    LoginFailureException.class,
                    () -> authService.login(request));

            assertEquals("INVALID_CREDENTIALS", exception.getCode());
            assertEquals(5 - attempt, exception.getRemainingAttempts());
            assertEquals(
                    attempt == 1 ? "Contraseña incorrecta. Te quedan 4 intentos."
                            : attempt == 2 ? "Contraseña incorrecta. Te quedan 3 intentos."
                            : attempt == 3 ? "Contraseña incorrecta. Te quedan 2 intentos."
                            : "Contraseña incorrecta. Te queda 1 intento.",
                    exception.getMessage());
        }

        assertEquals(4, user.getFailedAttempts());
        assertEquals(null, user.getLockedUntil());
        verify(userRepository, org.mockito.Mockito.times(4)).save(user);
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void locksAccountOnFifthFailureForThreeMinutes() {
        user.setFailedAttempts(4);
        when(passwordEncoder.matches(request.getPassword(), user.getPasswordHash())).thenReturn(false);
        OffsetDateTime before = OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(3);

        LoginFailureException exception = assertThrows(
                LoginFailureException.class,
                () -> authService.login(request));

        OffsetDateTime after = OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(3);
        assertEquals("ACCOUNT_TEMPORARILY_LOCKED", exception.getCode());
        assertEquals(5, user.getFailedAttempts());
        assertNotNull(user.getLockedUntil());
        assertEquals(exception.getLockedUntil(), user.getLockedUntil());
        org.junit.jupiter.api.Assertions.assertTrue(!user.getLockedUntil().isBefore(before));
        org.junit.jupiter.api.Assertions.assertTrue(!user.getLockedUntil().isAfter(after));
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void rejectsCorrectPasswordWithoutExtendingAnActiveLock() {
        OffsetDateTime lockedUntil = OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(2);
        user.setFailedAttempts(5);
        user.setLockedUntil(lockedUntil);

        LoginFailureException exception = assertThrows(
                LoginFailureException.class,
                () -> authService.login(request));

        assertEquals("ACCOUNT_TEMPORARILY_LOCKED", exception.getCode());
        assertEquals(5, user.getFailedAttempts());
        assertEquals(lockedUntil, user.getLockedUntil());
        verify(passwordEncoder, never()).matches(any(), any());
        verify(userRepository, never()).save(user);
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void expiredLockStartsNewFailureCycle() {
        user.setFailedAttempts(5);
        user.setLockedUntil(OffsetDateTime.now(ZoneOffset.UTC).minusSeconds(1));
        when(passwordEncoder.matches(request.getPassword(), user.getPasswordHash())).thenReturn(false);

        LoginFailureException exception = assertThrows(
                LoginFailureException.class,
                () -> authService.login(request));

        assertEquals("INVALID_CREDENTIALS", exception.getCode());
        assertEquals(1, user.getFailedAttempts());
        assertEquals(4, exception.getRemainingAttempts());
        assertEquals(null, user.getLockedUntil());
    }

    @Test
    void successfulLoginResetsFailureStateAndGeneratesJwt() {
        user.setFailedAttempts(4);
        user.setLockedUntil(null);
        request.setPassword("correct-password");
        when(passwordEncoder.matches(request.getPassword(), user.getPasswordHash())).thenReturn(true);
        when(jwtService.generateToken(any())).thenReturn("jwt-token");
        UserResponseDto responseUser = new UserResponseDto();
        AuthResponseDto response = AuthResponseDto.builder().token("jwt-token").user(responseUser).build();
        when(userMapper.toResponse(user)).thenReturn(responseUser);
        when(authMapper.toResponse("jwt-token", responseUser)).thenReturn(response);

        AuthResponseDto result = authService.login(request);

        assertEquals(response, result);
        assertEquals(0, user.getFailedAttempts());
        assertEquals(null, user.getLockedUntil());
        verify(userRepository).save(user);
        verify(jwtService).generateToken(any());
    }

    @Test
    void nonexistentEmailUsesGenericInvalidCredentialsWithoutUpdatingAUser() {
        when(userRepository.findForLoginByEmailAndDeletedAtIsNull(request.getEmail())).thenReturn(Optional.empty());

        LoginFailureException exception = assertThrows(
                LoginFailureException.class,
                () -> authService.login(request));

        assertEquals("INVALID_CREDENTIALS", exception.getCode());
        assertEquals(null, exception.getRemainingAttempts());
        assertEquals("Credenciales inválidas.", exception.getMessage());
        verify(passwordEncoder, never()).matches(any(), any());
        verify(userRepository, never()).save(any());
        verify(jwtService, never()).generateToken(any());
    }
}
