package com.sena.Backend_OneLanguage.accessibility.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "language", schema = "accessibility")
@Getter
@Setter
@NoArgsConstructor
public class Language {
    @Id
    @Column(name = "id_language", nullable = false, updatable = false)
    private UUID idLanguage;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 10)
    private String code;

    @Column(name = "is_default")
    private Boolean isDefault;

    @Column(name = "is_active")
    private Boolean isActive;
}
