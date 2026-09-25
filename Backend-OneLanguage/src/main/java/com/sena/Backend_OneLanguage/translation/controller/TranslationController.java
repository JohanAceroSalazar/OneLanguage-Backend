package com.sena.Backend_OneLanguage.translation.controller;

import com.sena.Backend_OneLanguage.security.model.CustomUserDetails;
import com.sena.Backend_OneLanguage.translation.dto.CreateTranslationRequestDto;
import com.sena.Backend_OneLanguage.translation.dto.TranslationResponseDto;
import com.sena.Backend_OneLanguage.translation.service.TranslationService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/translations")
@RequiredArgsConstructor
public class TranslationController {
    private final TranslationService translationService;

    @PostMapping
    public ResponseEntity<TranslationResponseDto> create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateTranslationRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(translationService.create(userDetails.getUser(), request));
    }

    @GetMapping
    public List<TranslationResponseDto> findAll(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return translationService.findForUser(userDetails.getUser());
    }

    @DeleteMapping("/{translationId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID translationId) {
        translationService.delete(userDetails.getUser(), translationId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll(@AuthenticationPrincipal CustomUserDetails userDetails) {
        translationService.deleteAll(userDetails.getUser());
        return ResponseEntity.noContent().build();
    }
}
