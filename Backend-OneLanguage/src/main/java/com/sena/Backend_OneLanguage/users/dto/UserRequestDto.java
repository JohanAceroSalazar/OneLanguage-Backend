//Información que llega desde el frontend
package com.sena.Backend_OneLanguage.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserRequestDto {

    @NotBlank(message = "El correo es obligatorio")
    @Pattern(
            regexp = "(?i)^(?!.*\\.\\.)(?!.*\\s)(?!.*@gail\\.com$)[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*@[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*\\.[A-Za-z]{2,}$",
            message = "El correo no tiene un formato válido")
    private String email;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Pattern(regexp = "^[\\p{L}]+(?: [\\p{L}]+)*$", message = "El nombre solo puede contener letras y espacios")
    private String fullName;

    @NotBlank(message = "La contrasena es obligatoria")
    private String password;

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public void setFullName(String fullName) {
        this.fullName = fullName == null ? null : fullName.trim();
    }
}
