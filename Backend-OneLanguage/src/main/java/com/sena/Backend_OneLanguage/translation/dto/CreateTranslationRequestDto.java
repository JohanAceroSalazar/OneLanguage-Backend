package com.sena.Backend_OneLanguage.translation.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateTranslationRequestDto {
    @NotBlank(message = "La traduccion no puede estar vacia")
    @Size(max = 2000, message = "La traduccion supera el limite permitido")
    private String translatedText;

    @DecimalMin(value = "0.0", message = "La confianza no puede ser negativa")
    @DecimalMax(value = "1.0", message = "La confianza no puede superar 1")
    private Float confidence;
}
