package com.sena.Backend_OneLanguage.accessibility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.sena.Backend_OneLanguage.accessibility.dto.AccessibilitySettingsRequestDto;
import com.sena.Backend_OneLanguage.accessibility.dto.AccessibilitySettingsResponseDto;
import com.sena.Backend_OneLanguage.accessibility.entity.AccessibilitySettings;
import com.sena.Backend_OneLanguage.accessibility.entity.Language;
import com.sena.Backend_OneLanguage.accessibility.repository.AccessibilitySettingsRepository;
import com.sena.Backend_OneLanguage.accessibility.repository.LanguageRepository;
import com.sena.Backend_OneLanguage.accessibility.service.AccessibilitySettingsService;
import com.sena.Backend_OneLanguage.users.entity.User;
import java.util.Optional;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AccessibilitySettingsServiceTest {
    @Mock AccessibilitySettingsRepository settingsRepository;
    @Mock LanguageRepository languageRepository;
    @InjectMocks AccessibilitySettingsService service;

    @Test
    void createsDefaultsForUserWithoutSettings() {
        User user = User.builder().idUser(UUID.randomUUID()).build();
        Language spanish = new Language();
        spanish.setCode("es");
        when(settingsRepository.findByUserIdUser(user.getIdUser())).thenReturn(Optional.empty());
        when(languageRepository.findByIsDefaultTrueAndIsActiveTrue()).thenReturn(List.of(spanish));
        when(settingsRepository.save(any(AccessibilitySettings.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AccessibilitySettingsResponseDto result = service.getOrCreate(user);

        assertEquals("es", result.language());
        assertEquals("medium", result.textSize());
        assertEquals("light", result.theme());
    }

    @Test
    void partialUpdatePreservesUnchangedPreferences() {
        User user = User.builder().idUser(UUID.randomUUID()).build();
        AccessibilitySettings existing = new AccessibilitySettings();
        existing.setUser(user);
        Language italian = new Language();
        italian.setCode("it");
        existing.setLanguage(italian);
        existing.setTextSize("large");
        existing.setThemeColor("dark");
        when(settingsRepository.findByUserIdUser(user.getIdUser())).thenReturn(Optional.of(existing));
        Language english = new Language();
        english.setCode("en");
        when(languageRepository.findByCodeAndIsActiveTrue("en")).thenReturn(Optional.of(english));
        when(settingsRepository.save(any(AccessibilitySettings.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AccessibilitySettingsResponseDto result = service.update(
                user, new AccessibilitySettingsRequestDto("en", null, null));

        assertEquals("en", result.language());
        assertEquals("large", result.textSize());
        assertEquals("dark", result.theme());
    }

    @Test
    void rejectsInactiveLanguage() {
        User user = User.builder().idUser(UUID.randomUUID()).build();
        AccessibilitySettings existing = new AccessibilitySettings();
        existing.setUser(user);
        when(settingsRepository.findByUserIdUser(user.getIdUser())).thenReturn(Optional.of(existing));
        when(languageRepository.findByCodeAndIsActiveTrue("it")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.update(user, new AccessibilitySettingsRequestDto("it", null, null)));

        assertEquals(400, exception.getStatusCode().value());
    }

    @Test
    void rejectsUnsupportedLanguage() {
        User user = User.builder().idUser(UUID.randomUUID()).build();
        AccessibilitySettings existing = new AccessibilitySettings();
        existing.setUser(user);
        when(settingsRepository.findByUserIdUser(user.getIdUser())).thenReturn(Optional.of(existing));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.update(user, new AccessibilitySettingsRequestDto("fr", null, null)));

        assertEquals(400, exception.getStatusCode().value());
    }

    @Test
    void rejectsMissingDefault() {
        User user = User.builder().idUser(UUID.randomUUID()).build();
        when(settingsRepository.findByUserIdUser(user.getIdUser())).thenReturn(Optional.empty());
        when(languageRepository.findByIsDefaultTrueAndIsActiveTrue()).thenReturn(List.of());

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> service.getOrCreate(user));

        assertEquals("No existe un idioma predeterminado activo en accessibility.language", exception.getMessage());
    }

    @Test
    void rejectsMultipleDefaults() {
        User user = User.builder().idUser(UUID.randomUUID()).build();
        when(settingsRepository.findByUserIdUser(user.getIdUser())).thenReturn(Optional.empty());
        when(languageRepository.findByIsDefaultTrueAndIsActiveTrue()).thenReturn(List.of(new Language(), new Language()));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> service.getOrCreate(user));

        assertEquals("La configuración de idiomas contiene múltiples defaults activos", exception.getMessage());
    }

}
