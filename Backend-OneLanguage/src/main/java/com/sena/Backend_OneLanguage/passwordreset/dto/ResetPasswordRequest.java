package com.sena.Backend_OneLanguage.passwordreset.dto;

import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {

    @NotNull(message = "El identificador del token es obligatorio.")
    private UUID tokenIdentifier;

    @NotBlank(message = "El token es obligatorio.")
    private String token;

    @NotBlank(message = "La nueva contraseña es obligatoria.")
    private String newPassword;

    @NotBlank(message = "La confirmación de contraseña es obligatoria.")
    private String confirmPassword;
}