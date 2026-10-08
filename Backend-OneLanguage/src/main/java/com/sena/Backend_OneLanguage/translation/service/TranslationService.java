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
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class TranslationService {
    private final TranslationRepository translationRepository;
    private final TranslationRecordingStorage recordingStorage;

    public TranslationResponseDto create(User user, CreateTranslationRequestDto request) {
        return toResponse(saveTranslation(user, request));
    }

    public TranslationResponseDto createWithRecording(User user, CreateTranslationRequestDto request, MultipartFile recording) {
        Translation translation = saveTranslation(user, request);
        TranslationRecordingStorage.StoredRecording stored = recordingStorage.store(translation.getIdTranslation(), recording);
        translation.setRecordingPath(stored.path());
        translation.setRecordingContentType(stored.contentType());
        translation.setRecordingSize(stored.size());
        return toResponse(translationRepository.save(translation));
    }

    private Translation saveTranslation(User user, CreateTranslationRequestDto request) {
        Translation translation = new Translation();
        translation.setUser(user);
        translation.setInputType("camera");
        translation.setTranslatedText(request.getTranslatedText().trim());
        translation.setConfidence(request.getConfidence());
        translation.setTranslationStatus("completed");
        return translationRepository.save(translation);
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
        recordingStorage.delete(translation.getRecordingPath());
        translation.setDeletedAt(OffsetDateTime.now(ZoneOffset.UTC));
        translationRepository.save(translation);
    }

    public void deleteAll(User user) {
        List<Translation> translations = translationRepository
                .findAllByUserIdUserAndDeletedAtIsNullOrderByCreatedAtDesc(user.getIdUser());
        OffsetDateTime deletedAt = OffsetDateTime.now(ZoneOffset.UTC);
        translations.forEach(translation -> {
            recordingStorage.delete(translation.getRecordingPath());
            translation.setDeletedAt(deletedAt);
        });
        translationRepository.saveAll(translations);
    }

    @Transactional(readOnly = true)
    public RecordingResource getRecording(User user, UUID translationId) {
        Translation translation = translationRepository
                .findByIdTranslationAndUserIdUserAndDeletedAtIsNull(translationId, user.getIdUser())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Traduccion no encontrada"));
        if (translation.getRecordingPath() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La traduccion no tiene grabacion");
        }
        return new RecordingResource(
                recordingStorage.load(translation.getRecordingPath()),
                translation.getRecordingContentType(),
                translation.getRecordingSize());
    }

    private TranslationResponseDto toResponse(Translation translation) {
        return TranslationResponseDto.builder()
                .id(translation.getIdTranslation())
                .translatedText(translation.getTranslatedText())
                .confidence(translation.getConfidence())
                .hasRecording(translation.getRecordingPath() != null)
                .createdAt(translation.getCreatedAt())
                .build();
    }

    public record RecordingResource(Resource resource, String contentType, Long size) {}
}
