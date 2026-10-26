package com.sena.Backend_OneLanguage.permissions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sena.Backend_OneLanguage.permissions.dto.FeaturePermissionsResponseDto;
import com.sena.Backend_OneLanguage.permissions.dto.UpdateFeaturePermissionRequestDto;
import com.sena.Backend_OneLanguage.permissions.entity.Permission;
import com.sena.Backend_OneLanguage.permissions.entity.UserPermission;
import com.sena.Backend_OneLanguage.permissions.entity.UserPermissionId;
import com.sena.Backend_OneLanguage.permissions.repository.PermissionRepository;
import com.sena.Backend_OneLanguage.permissions.repository.UserPermissionRepository;
import com.sena.Backend_OneLanguage.permissions.service.FeaturePermissionService;
import com.sena.Backend_OneLanguage.users.entity.User;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FeaturePermissionServiceTest {
    @Mock PermissionRepository permissionRepository;
    @Mock UserPermissionRepository userPermissionRepository;
    @InjectMocks FeaturePermissionService service;

    @Test
    void returnsOnlyCameraAndAudioPermissions() {
        User user = User.builder().idUser(UUID.randomUUID()).build();
        Permission camera = permission("camera");
        Permission audio = permission("audio");
        when(permissionRepository.findByPermissionNameIn(any())).thenReturn(List.of(camera, audio));
        when(userPermissionRepository.findByUserIdUser(user.getIdUser()))
                .thenReturn(List.of(userPermission(user, camera, "enabled"), userPermission(user, audio, "disabled")));

        FeaturePermissionsResponseDto result = service.getOrCreate(user);

        assertTrue(result.camera());
        assertFalse(result.audio());
    }

    @Test
    void updatesCameraWithoutCreatingRemovedPermission() {
        User user = User.builder().idUser(UUID.randomUUID()).build();
        Permission camera = permission("camera");
        Permission audio = permission("audio");
        UserPermission cameraPermission = userPermission(user, camera, "disabled");
        when(permissionRepository.findByPermissionNameIn(any())).thenReturn(List.of(camera, audio));
        when(userPermissionRepository.findByUserIdUser(user.getIdUser()))
                .thenReturn(List.of(cameraPermission, userPermission(user, audio, "disabled")));

        FeaturePermissionsResponseDto result = service.update(
                user, new UpdateFeaturePermissionRequestDto("camera", true));

        assertTrue(result.camera());
        assertFalse(result.audio());
        verify(userPermissionRepository).save(cameraPermission);
    }

    private Permission permission(String name) {
        Permission permission = new Permission();
        permission.setIdPermission(UUID.randomUUID());
        permission.setPermissionName(name);
        return permission;
    }

    private UserPermission userPermission(User user, Permission permission, String status) {
        UserPermission result = new UserPermission();
        result.setId(new UserPermissionId(user.getIdUser(), permission.getIdPermission()));
        result.setUser(user);
        result.setPermission(permission);
        result.setStatus(status);
        return result;
    }
}
