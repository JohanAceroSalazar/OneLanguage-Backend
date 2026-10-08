package com.sena.Backend_OneLanguage.translation.controller;

import com.sena.Backend_OneLanguage.security.model.CustomUserDetails;
import com.sena.Backend_OneLanguage.translation.dto.CreateTranslationRequestDto;
import com.sena.Backend_OneLanguage.translation.dto.TranslationResponseDto;
import com.sena.Backend_OneLanguage.translation.service.TranslationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/translations")
@RequiredArgsConstructor
@Validated
public class TranslationController {
    private final TranslationService translationService;

    @PostMapping
    public ResponseEntity<TranslationResponseDto> create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateTranslationRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(translationService.create(userDetails.getUser(), request));
    }

    @PostMapping(value = "/with-recording", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TranslationResponseDto> createWithRecording(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @NotBlank @Size(max = 2000) String translatedText,
            @RequestParam(required = false) @DecimalMin("0.0") @DecimalMax("1.0") Float confidence,
            @RequestPart("recording") MultipartFile recording) {
        CreateTranslationRequestDto request = new CreateTranslationRequestDto();
        request.setTranslatedText(translatedText);
        request.setConfidence(confidence);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(translationService.createWithRecording(userDetails.getUser(), request, recording));
    }

    @GetMapping
    public List<TranslationResponseDto> findAll(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return translationService.findForUser(userDetails.getUser());
    }

    @GetMapping("/{translationId}/recording")
    public ResponseEntity<Resource> getRecording(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID translationId) {
        TranslationService.RecordingResource recording = translationService.getRecording(userDetails.getUser(), translationId);
        MediaType contentType = MediaType.parseMediaType(recording.contentType());
        ResponseEntity.BodyBuilder response = ResponseEntity.ok().contentType(contentType);
        if (recording.size() != null) response.contentLength(recording.size());
        return response.body(recording.resource());
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
