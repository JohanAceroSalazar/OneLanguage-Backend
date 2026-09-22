package com.sena.Backend_OneLanguage.translation.service;

import com.sena.Backend_OneLanguage.translation.dto.CreateTranslationRequestDto;
import com.sena.Backend_OneLanguage.translation.dto.TranslationResponseDto;
import com.sena.Backend_OneLanguage.translation.entity.Translation;
import com.sena.Backend_OneLanguage.translation.repository.TranslationRepository;
import com.sena.Backend_OneLanguage.users.entity.User;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class TranslationService {
    private final TranslationRepository translationRepository;

    public TranslationResponseDto create(User user, CreateTranslationRequestDto request) {
        Translation translation = new Translation();
        translation.setUser(user);
        translation.setInputType("camera");
        translation.setTranslatedText(request.getTranslatedText().trim());
        translation.setConfidence(request.getConfidence());
        translation.setTranslationStatus("completed");
        return toResponse(translationRepository.save(translation));
    }

    @Transactional(readOnly = true)
    public List<TranslationResponseDto> findForUser(User user) {
        return translationRepository.findAllByUserIdUserAndDeletedAtIsNullOrderByCreatedAtDesc(user.getIdUser())
                .stream().map(this::toResponse).toList();
    }

    public void delete(User user, UUID translationId) {
        Translation translation = translationRepository
                .findByIdTranslationAndUserIdUserAndDeletedAtIsNull(translationId, user.getIdUser())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Traduccion no encontrada"));
        translation.setDeletedAt(OffsetDateTime.now(ZoneOffset.UTC));
        translationRepository.save(translation);
    }

    public void deleteAll(User user) {
        List<Translation> translations = translationRepository
                .findAllByUserIdUserAndDeletedAtIsNullOrderByCreatedAtDesc(user.getIdUser());
        OffsetDateTime deletedAt = OffsetDateTime.now(ZoneOffset.UTC);
        translations.forEach(translation -> translation.setDeletedAt(deletedAt));
        translationRepository.saveAll(translations);
    }

    private TranslationResponseDto toResponse(Translation translation) {
        return TranslationResponseDto.builder()
                .id(translation.getIdTranslation())
                .translatedText(translation.getTranslatedText())
                .confidence(translation.getConfidence())
                .createdAt(translation.getCreatedAt())
                .build();
    }
}
