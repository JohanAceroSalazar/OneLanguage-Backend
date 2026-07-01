package com.sena.Backend_OneLanguage.users.mapper;

import com.sena.Backend_OneLanguage.users.dto.UserRequestDto;
import com.sena.Backend_OneLanguage.users.dto.UserResponseDto;
import com.sena.Backend_OneLanguage.users.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User toEntity(UserRequestDto request) {
        if (request == null) {
            return null;
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        return user;
    }

    public UserResponseDto toResponse(User user) {
        if (user == null) {
            return null;
        }

        UserResponseDto response = new UserResponseDto();
        response.setIdUser(user.getIdUser());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        return response;
    }

    public List<UserResponseDto> toResponseList(List<User> users) {
        return users.stream()
                .map(this::toResponse)
                .toList();
    }
}
