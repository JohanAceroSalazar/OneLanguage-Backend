package com.sena.Backend_OneLanguage.translation.dto;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TranslationResponseDto {
    UUID id;
    String translatedText;
    Float confidence;
    OffsetDateTime createdAt;
}
