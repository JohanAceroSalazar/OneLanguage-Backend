package com.sena.Backend_OneLanguage.accessibility.entity;

import com.sena.Backend_OneLanguage.users.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "accessibility_settings", schema = "accessibility")
@Getter
@Setter
@NoArgsConstructor
public class AccessibilitySettings {
    @Id
    @Column(name = "id_settings", nullable = false, updatable = false)
    private UUID idSettings;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_user", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_language", nullable = false)
    private Language language;

    @Column(name = "text_size", length = 50)
    private String textSize;

    @Column(name = "theme_color", length = 20)
    private String themeColor;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
