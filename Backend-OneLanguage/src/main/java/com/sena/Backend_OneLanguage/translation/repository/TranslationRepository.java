package com.sena.Backend_OneLanguage.translation.repository;

import com.sena.Backend_OneLanguage.translation.entity.Translation;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TranslationRepository extends JpaRepository<Translation, UUID> {
    List<Translation> findAllByUserIdUserAndDeletedAtIsNullOrderByCreatedAtDesc(UUID userId);

    Optional<Translation> findByIdTranslationAndUserIdUserAndDeletedAtIsNull(UUID translationId, UUID userId);
}
