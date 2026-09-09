package com.sena.Backend_OneLanguage.users.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sena.Backend_OneLanguage.users.dto.UserRequestDto;
import com.sena.Backend_OneLanguage.users.dto.UserResponseDto;
import com.sena.Backend_OneLanguage.users.entity.User;
import com.sena.Backend_OneLanguage.users.mapper.UserMapper;
import com.sena.Backend_OneLanguage.users.repository.UserRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createsUserWithEncodedPassword() {
        UserRequestDto request = new UserRequestDto();
        request.setEmail("user@example.com");
        request.setFullName("Usuario de prueba");
        request.setPassword("plain-password");

        User user = User.builder().email(request.getEmail()).fullName(request.getFullName()).build();
        UserResponseDto response = new UserResponseDto();
        response.setIdUser(UUID.randomUUID());

        when(userRepository.existsByEmailAndDeletedAtIsNull(request.getEmail())).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-password");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponseDto result = userService.create(request);

        assertEquals(response, result);
        assertEquals("encoded-password", user.getPasswordHash());
        verify(userRepository).save(user);
    }

    @Test
    void rejectsDuplicateEmail() {
        UserRequestDto request = new UserRequestDto();
        request.setEmail("existing@example.com");

        when(userRepository.existsByEmailAndDeletedAtIsNull(request.getEmail())).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.create(request));

        assertEquals(409, exception.getStatusCode().value());
    }
}
