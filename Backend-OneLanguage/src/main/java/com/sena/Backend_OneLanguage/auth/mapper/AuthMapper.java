package com.sena.Backend_OneLanguage.auth.mapper;

import org.springframework.stereotype.Component;
import com.sena.Backend_OneLanguage.auth.dto.AuthResponseDto;
import com.sena.Backend_OneLanguage.users.dto.UserResponseDto;

@Component
public class AuthMapper {

    public AuthResponseDto toResponse(String token, UserResponseDto userResponse){

        return AuthResponseDto.builder()
            .token(token)
            .user(userResponse)
            .build();
    }
}