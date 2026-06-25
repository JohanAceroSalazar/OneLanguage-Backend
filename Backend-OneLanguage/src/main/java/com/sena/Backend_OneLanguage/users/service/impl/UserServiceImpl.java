//Aquí vive la lógica de negocio
package com.sena.Backend_OneLanguage.users.service.impl;

import com.sena.Backend_OneLanguage.users.dto.UserRequestDto;
import com.sena.Backend_OneLanguage.users.dto.UserResponseDto;
import com.sena.Backend_OneLanguage.users.entity.User;
import com.sena.Backend_OneLanguage.users.mapper.UserMapper;
import com.sena.Backend_OneLanguage.users.repository.UserRepository;
import com.sena.Backend_OneLanguage.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto create(UserRequestDto request) {
        if (userRepository.existsByEmailAndDeletedAtIsNull(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con ese correo");
        }

        User user = userMapper.toEntity(request);
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto findById(UUID id) {
        User user = userRepository.findByIdUserAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontro el usuario con el id: " + id));

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll() {
        return userMapper.toResponseList(userRepository.findAllByDeletedAtIsNull());
    }

    @Override
    public void delete(UUID id) {
        User user = userRepository.findByIdUserAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontro el usuario con el id: " + id));

        user.setDeletedAt(java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC));
        user.setUserStatus(Boolean.FALSE);
        userRepository.save(user);
    }
}
