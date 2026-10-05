package com.sena.Backend_OneLanguage.passwordreset.service.imple;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sena.Backend_OneLanguage.email.service.EmailService;
import com.sena.Backend_OneLanguage.passwordreset.dto.ForgotPasswordRequest;
import com.sena.Backend_OneLanguage.passwordreset.entity.PasswordResetToken;
import com.sena.Backend_OneLanguage.passwordreset.repository.PasswordResetTokenRepository;
import com.sena.Backend_OneLanguage.users.entity.User;
import com.sena.Backend_OneLanguage.users.repository.UserRepository;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PasswordResetServiceImpl passwordResetService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(passwordResetService, "frontendBaseUrl", "http://localhost:5173");
    }

    @Test
    void allowsPasswordRecoveryForTemporarilyLockedAccount() {
        User user = User.builder()
                .email("locked@example.com")
                .fullName("Locked User")
                .passwordHash("encoded-password")
                .failedAttempts(5)
                .lockedUntil(OffsetDateTime.now().plusMinutes(2))
                .userStatus(true)
                .build();
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail(user.getEmail());

        when(userRepository.findByEmailAndDeletedAtIsNull(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(any(String.class))).thenReturn("hashed-token");

        passwordResetService.forgotPassword(request);

        verify(passwordResetTokenRepository).deleteAllByUser(user);
        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).sendPasswordResetEmail(eq(user.getEmail()), eq(user.getFullName()), any(String.class));
    }

    @Test
    void buildsResetLinkFromConfiguredFrontendBaseUrl() {
        ReflectionTestUtils.setField(passwordResetService, "frontendBaseUrl", "https://lan-host.test:5173/");

        User user = User.builder()
                .email("user@example.com")
                .fullName("Test User")
                .passwordHash("encoded-password")
                .userStatus(true)
                .build();
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail(user.getEmail());

        when(userRepository.findByEmailAndDeletedAtIsNull(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(any(String.class))).thenReturn("hashed-token");

        passwordResetService.forgotPassword(request);

        ArgumentCaptor<String> resetLinkCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService).sendPasswordResetEmail(
                eq(user.getEmail()),
                eq(user.getFullName()),
                resetLinkCaptor.capture());

        String resetLink = resetLinkCaptor.getValue();
        assertTrue(resetLink.startsWith("https://lan-host.test:5173/reset-password?id="));
        assertTrue(resetLink.contains("&token="));
    }
}
