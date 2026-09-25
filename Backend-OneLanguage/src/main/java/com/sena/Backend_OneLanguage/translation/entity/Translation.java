package com.sena.Backend_OneLanguage.translation.entity;

import com.sena.Backend_OneLanguage.users.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "translations", schema = "translation")
@Getter
@Setter
public class Translation {

    @Id
    @Column(name = "id_translation", nullable = false, updatable = false)
    private UUID idTranslation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @Column(name = "input_type", nullable = false, length = 20)
    private String inputType;

    @Column(name = "translated_text", nullable = false)
    private String translatedText;

    @Column
    private Float confidence;

    @Column(name = "processing_time")
    private Float processingTime;

    @Column(name = "translation_status", length = 20)
    private String translationStatus;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @PrePersist
    void onCreate() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        if (idTranslation == null) idTranslation = UUID.randomUUID();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }
}
