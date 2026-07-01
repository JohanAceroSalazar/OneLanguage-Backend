package com.sena.Backend_OneLanguage.auth.service;

import com.sena.Backend_OneLanguage.auth.dto.AuthResponseDto;
import com.sena.Backend_OneLanguage.auth.dto.LoginRequestDto;

public interface AuthService {

    /**
     * Autentica un usuario y genera un JWT.
     *
     * @param request Datos de inicio de sesión.
     * @return Token JWT junto con la información del usuario.
     */
    AuthResponseDto login(LoginRequestDto request);
}