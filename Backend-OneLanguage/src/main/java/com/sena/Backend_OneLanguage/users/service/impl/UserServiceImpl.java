//Aquí vive la lógica de negocio
package com.sena.Backend_OneLanguage.users.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.sena.Backend_OneLanguage.users.dto.UserRequestDto;
import com.sena.Backend_OneLanguage.users.dto.UserResponseDto;
import com.sena.Backend_OneLanguage.users.entity.User;
import com.sena.Backend_OneLanguage.users.mapper.UserMapper;
import com.sena.Backend_OneLanguage.users.repository.UserRepository;
import com.sena.Backend_OneLanguage.users.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto create(UserRequestDto request) {
        if (userRepository.existsByEmailAndDeletedAtIsNull(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con ese correo");
        }

        User user = userMapper.toEntity(request);

        user.setPasswordHash(
            passwordEncoder.encode(request.getPassword())
        );

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto currentUser(User user) {
        return userMapper.toResponse(user);
    }

}
