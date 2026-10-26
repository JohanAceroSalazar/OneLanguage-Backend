package com.sena.Backend_OneLanguage.accessibility.dto;

import jakarta.validation.constraints.Pattern;

public record AccessibilitySettingsRequestDto(
        @Pattern(regexp = "es|en|pt|it", message = "El idioma debe ser es, en, pt o it")
        String language,
        @Pattern(regexp = "small|medium|large", message = "El tamaño de texto no es válido")
        String textSize,
        @Pattern(regexp = "light|dark", message = "El tema no es válido")
        String theme) {}
