package com.sena.Backend_OneLanguage.permissions.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UpdateFeaturePermissionRequestDto(
        @NotBlank @Pattern(regexp = "camera|audio") String feature,
        @NotNull Boolean enabled) {
}
