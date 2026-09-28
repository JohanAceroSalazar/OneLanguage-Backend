package com.sena.Backend_OneLanguage.accessibility.service;

import com.sena.Backend_OneLanguage.accessibility.dto.AccessibilitySettingsRequestDto;
import com.sena.Backend_OneLanguage.accessibility.dto.AccessibilitySettingsResponseDto;
import com.sena.Backend_OneLanguage.accessibility.entity.AccessibilitySettings;
import com.sena.Backend_OneLanguage.accessibility.entity.Language;
import com.sena.Backend_OneLanguage.accessibility.repository.AccessibilitySettingsRepository;
import com.sena.Backend_OneLanguage.accessibility.repository.LanguageRepository;
import com.sena.Backend_OneLanguage.users.entity.User;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccessibilitySettingsService {
    private static final String DEFAULT_LANGUAGE = "es";
    private static final String DEFAULT_TEXT_SIZE = "medium";
    private static final String DEFAULT_THEME = "light";

    private final AccessibilitySettingsRepository settingsRepository;
    private final LanguageRepository languageRepository;

    @Transactional
    public AccessibilitySettingsResponseDto getOrCreate(User user) {
        AccessibilitySettings settings = settingsRepository.findByUserIdUser(user.getIdUser())
                .orElseGet(() -> createDefaults(user));
        return toResponse(settings);
    }

    @Transactional
    public AccessibilitySettingsResponseDto update(User user, AccessibilitySettingsRequestDto request) {
        AccessibilitySettings settings = settingsRepository.findByUserIdUser(user.getIdUser())
                .orElseGet(() -> createDefaults(user));

        if (request.language() != null) {
            settings.setLanguage(languageRepository.findByCode(request.language())
                    .orElseThrow(() -> new IllegalArgumentException("El idioma no está configurado")));
        }
        if (request.textSize() != null) settings.setTextSize(request.textSize());
        if (request.theme() != null) settings.setThemeColor(request.theme());
        settings.setUpdatedAt(LocalDateTime.now());
        return toResponse(settingsRepository.save(settings));
    }

    private AccessibilitySettings createDefaults(User user) {
        Language language = languageRepository.findByCode(DEFAULT_LANGUAGE)
                .orElseThrow(() -> new IllegalStateException("No existe el idioma predeterminado"));
        AccessibilitySettings settings = new AccessibilitySettings();
        settings.setIdSettings(UUID.randomUUID());
        settings.setUser(user);
        settings.setLanguage(language);
        settings.setTextSize(DEFAULT_TEXT_SIZE);
        settings.setThemeColor(DEFAULT_THEME);
        settings.setCreatedAt(LocalDateTime.now());
        settings.setUpdatedAt(settings.getCreatedAt());
        return settingsRepository.save(settings);
    }

    private AccessibilitySettingsResponseDto toResponse(AccessibilitySettings settings) {
        return new AccessibilitySettingsResponseDto(
                settings.getLanguage().getCode(), settings.getTextSize(), settings.getThemeColor());
    }
}
