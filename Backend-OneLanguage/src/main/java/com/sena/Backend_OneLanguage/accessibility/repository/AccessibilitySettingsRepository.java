package com.sena.Backend_OneLanguage.accessibility.repository;

import com.sena.Backend_OneLanguage.accessibility.entity.AccessibilitySettings;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessibilitySettingsRepository extends JpaRepository<AccessibilitySettings, UUID> {
    Optional<AccessibilitySettings> findByUserIdUser(UUID userId);
}
