package com.sena.Backend_OneLanguage.permissions.controller;

import com.sena.Backend_OneLanguage.permissions.dto.FeaturePermissionsResponseDto;
import com.sena.Backend_OneLanguage.permissions.dto.UpdateFeaturePermissionRequestDto;
import com.sena.Backend_OneLanguage.permissions.service.FeaturePermissionService;
import com.sena.Backend_OneLanguage.security.model.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feature-permissions")
@RequiredArgsConstructor
public class FeaturePermissionController {
    private final FeaturePermissionService service;

    @GetMapping
    public ResponseEntity<FeaturePermissionsResponseDto> get(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(service.getOrCreate(userDetails.getUser()));
    }

    @PutMapping
    public ResponseEntity<FeaturePermissionsResponseDto> update(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateFeaturePermissionRequestDto request) {
        return ResponseEntity.ok(service.update(userDetails.getUser(), request));
    }
}
