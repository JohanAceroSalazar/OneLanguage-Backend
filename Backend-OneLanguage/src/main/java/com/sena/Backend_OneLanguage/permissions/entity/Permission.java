package com.sena.Backend_OneLanguage.permissions.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "permissions", schema = "auth")
@Getter
@Setter
@NoArgsConstructor
public class Permission {

    @Id
    @Column(name = "id_permission", nullable = false, updatable = false)
    private UUID idPermission;

    @Column(name = "permission_name", nullable = false, unique = true)
    private String permissionName;

    @Column
    private String description;
}
