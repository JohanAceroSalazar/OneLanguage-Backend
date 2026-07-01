package com.sena.Backend_OneLanguage.auth.dto;

import com.sena.Backend_OneLanguage.users.dto.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {

    private String token;
    private UserResponseDto user;
}