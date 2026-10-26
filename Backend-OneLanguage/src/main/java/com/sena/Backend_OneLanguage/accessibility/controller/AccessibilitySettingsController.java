package com.sena.Backend_OneLanguage.accessibility.controller;

import com.sena.Backend_OneLanguage.accessibility.dto.AccessibilitySettingsRequestDto;
import com.sena.Backend_OneLanguage.accessibility.dto.AccessibilitySettingsResponseDto;
import com.sena.Backend_OneLanguage.accessibility.service.AccessibilitySettingsService;
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
@RequestMapping("/api/accessibility-settings")
@RequiredArgsConstructor
public class AccessibilitySettingsController {
    private final AccessibilitySettingsService service;

    @GetMapping
    public ResponseEntity<AccessibilitySettingsResponseDto> get(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(service.getOrCreate(userDetails.getUser()));
    }

    @PutMapping
    public ResponseEntity<AccessibilitySettingsResponseDto> update(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AccessibilitySettingsRequestDto request) {
        return ResponseEntity.ok(service.update(userDetails.getUser(), request));
    }
}
