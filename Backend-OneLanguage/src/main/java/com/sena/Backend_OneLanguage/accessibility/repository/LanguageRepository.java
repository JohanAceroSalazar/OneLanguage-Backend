package com.sena.Backend_OneLanguage.accessibility.repository;

import com.sena.Backend_OneLanguage.accessibility.entity.Language;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, UUID> {
    Optional<Language> findByCode(String code);

    Optional<Language> findByCodeAndIsActiveTrue(String code);

    List<Language> findByIsDefaultTrueAndIsActiveTrue();
}
