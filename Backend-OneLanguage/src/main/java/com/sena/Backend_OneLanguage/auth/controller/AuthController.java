package com.sena.Backend_OneLanguage.auth.controller;

import com.sena.Backend_OneLanguage.auth.dto.AuthResponseDto;
import com.sena.Backend_OneLanguage.auth.dto.LoginRequestDto;
import com.sena.Backend_OneLanguage.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(
            @Valid @RequestBody LoginRequestDto request) {

        return ResponseEntity.ok(authService.login(request));
    }
}