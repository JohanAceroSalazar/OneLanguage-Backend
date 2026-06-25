//Información que se envía al frontend
package com.sena.Backend_OneLanguage.users.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class UserResponseDto {
    private UUID idUser;
    private String fullName;
    private String email;
}
