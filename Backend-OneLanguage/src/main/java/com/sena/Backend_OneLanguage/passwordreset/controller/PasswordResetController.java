package com.sena.Backend_OneLanguage.passwordreset.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.sena.Backend_OneLanguage.passwordreset.dto.ForgotPasswordRequest;
import com.sena.Backend_OneLanguage.passwordreset.dto.ResetPasswordRequest;
import com.sena.Backend_OneLanguage.passwordreset.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        passwordResetService.forgotPassword(request);

        return ResponseEntity.ok("Se envió el enlace de recuperación al correo electrónico.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        passwordResetService.resetPassword(request);

        return ResponseEntity.ok("La contraseña se actualizó correctamente.");
    }

}