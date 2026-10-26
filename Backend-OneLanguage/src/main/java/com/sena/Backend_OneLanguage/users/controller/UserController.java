package com.sena.Backend_OneLanguage.users.controller;

import com.sena.Backend_OneLanguage.users.dto.UserRequestDto;
import com.sena.Backend_OneLanguage.users.dto.UserResponseDto;
import com.sena.Backend_OneLanguage.users.service.UserService;
import com.sena.Backend_OneLanguage.security.model.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> currentUser(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userService.currentUser(userDetails.getUser()));
    }

}
