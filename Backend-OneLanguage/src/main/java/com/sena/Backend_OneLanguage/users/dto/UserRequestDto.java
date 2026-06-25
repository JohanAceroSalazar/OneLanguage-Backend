//Información que llega desde el frontend
package com.sena.Backend_OneLanguage.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequestDto {

    @Email(message = "El correo no tiene un formato valido")
    @NotBlank(message = "El correo es obligatorio")
    private String email;

    @NotBlank(message = "El nombre completo es obligatorio")
    private String fullName;

    @NotBlank(message = "La contrasena es obligatoria")
    private String passwordHash;
}
