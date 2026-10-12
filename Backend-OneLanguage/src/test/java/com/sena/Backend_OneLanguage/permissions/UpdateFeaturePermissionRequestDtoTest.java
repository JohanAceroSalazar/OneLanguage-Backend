package com.sena.Backend_OneLanguage.permissions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sena.Backend_OneLanguage.permissions.dto.UpdateFeaturePermissionRequestDto;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

class UpdateFeaturePermissionRequestDtoTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void acceptsCameraAndAudioOnly() {
        assertTrue(validator.validate(new UpdateFeaturePermissionRequestDto("camera", true)).isEmpty());
        assertTrue(validator.validate(new UpdateFeaturePermissionRequestDto("audio", false)).isEmpty());
        assertFalse(validator.validate(new UpdateFeaturePermissionRequestDto("files", true)).isEmpty());
    }
}
