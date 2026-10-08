package com.sena.Backend_OneLanguage.permissions.service;

import com.sena.Backend_OneLanguage.permissions.dto.FeaturePermissionsResponseDto;
import com.sena.Backend_OneLanguage.permissions.dto.UpdateFeaturePermissionRequestDto;
import com.sena.Backend_OneLanguage.permissions.entity.Permission;
import com.sena.Backend_OneLanguage.permissions.entity.UserPermission;
import com.sena.Backend_OneLanguage.permissions.entity.UserPermissionId;
import com.sena.Backend_OneLanguage.permissions.repository.PermissionRepository;
import com.sena.Backend_OneLanguage.permissions.repository.UserPermissionRepository;
import com.sena.Backend_OneLanguage.users.entity.User;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class FeaturePermissionService {
    private static final Set<String> FEATURES = Set.of("camera", "audio", "files");
    private static final String ENABLED = "enabled";
    private static final String DISABLED = "disabled";

    private final PermissionRepository permissionRepository;
    private final UserPermissionRepository userPermissionRepository;

    @Transactional
    public FeaturePermissionsResponseDto getOrCreate(User user) {
        return toResponse(ensurePermissions(user));
    }

    @Transactional
    public FeaturePermissionsResponseDto update(User user, UpdateFeaturePermissionRequestDto request) {
        Map<String, UserPermission> permissions = ensurePermissions(user);
        UserPermission userPermission = permissions.get(request.feature());
        boolean enabled = request.enabled();
        userPermission.setStatus(enabled ? ENABLED : DISABLED);
        userPermission.setGrantedAt(enabled ? OffsetDateTime.now(ZoneOffset.UTC) : null);
        userPermissionRepository.save(userPermission);
        return toResponse(permissions);
    }

    private Map<String, UserPermission> ensurePermissions(User user) {
        Map<String, Permission> catalog = permissionRepository.findByPermissionNameIn(FEATURES).stream()
                .collect(Collectors.toMap(Permission::getPermissionName, Function.identity()));
        if (catalog.size() != FEATURES.size()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "El catálogo de permisos no está configurado");
        }

        Map<String, UserPermission> existing = userPermissionRepository.findByUserIdUser(user.getIdUser()).stream()
                .filter(entry -> FEATURES.contains(entry.getPermission().getPermissionName()))
                .collect(Collectors.toMap(entry -> entry.getPermission().getPermissionName(), Function.identity()));

        List<UserPermission> missing = FEATURES.stream()
                .filter(feature -> !existing.containsKey(feature))
                .map(feature -> newPermission(user, catalog.get(feature)))
                .toList();
        if (!missing.isEmpty()) {
            userPermissionRepository.saveAll(missing);
            missing.forEach(entry -> existing.put(entry.getPermission().getPermissionName(), entry));
        }
        return existing;
    }

    private UserPermission newPermission(User user, Permission permission) {
        UserPermission userPermission = new UserPermission();
        userPermission.setId(new UserPermissionId(user.getIdUser(), permission.getIdPermission()));
        userPermission.setUser(user);
        userPermission.setPermission(permission);
        userPermission.setStatus(DISABLED);
        return userPermission;
    }

    private FeaturePermissionsResponseDto toResponse(Map<String, UserPermission> permissions) {
        return new FeaturePermissionsResponseDto(
                ENABLED.equals(permissions.get("camera").getStatus()),
                ENABLED.equals(permissions.get("audio").getStatus()),
                ENABLED.equals(permissions.get("files").getStatus()));
    }
}
