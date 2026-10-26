package com.sena.Backend_OneLanguage.permissions.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserPermissionId implements Serializable {

    @Column(name = "id_user")
    private UUID userId;

    @Column(name = "id_permission")
    private UUID permissionId;
}
