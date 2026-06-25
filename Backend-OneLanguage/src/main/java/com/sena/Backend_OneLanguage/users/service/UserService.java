//Contrato del servicio
package com.sena.Backend_OneLanguage.users.service;

import com.sena.Backend_OneLanguage.users.dto.UserRequestDto;
import com.sena.Backend_OneLanguage.users.dto.UserResponseDto;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponseDto create(UserRequestDto request);

    UserResponseDto findById(UUID id);

    List<UserResponseDto> findAll();

    void delete(UUID id);
}
