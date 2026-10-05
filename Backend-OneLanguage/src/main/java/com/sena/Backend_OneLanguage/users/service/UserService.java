//Contrato del servicio
package com.sena.Backend_OneLanguage.users.service;

import com.sena.Backend_OneLanguage.users.dto.UserRequestDto;
import com.sena.Backend_OneLanguage.users.dto.UserResponseDto;
import com.sena.Backend_OneLanguage.users.entity.User;

public interface UserService {

    UserResponseDto create(UserRequestDto request);

    UserResponseDto currentUser(User user);
}
