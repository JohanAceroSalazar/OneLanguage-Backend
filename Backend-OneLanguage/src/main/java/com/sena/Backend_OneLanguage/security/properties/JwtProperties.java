package com.sena.Backend_OneLanguage.security.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@Component
@Validated
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * Clave secreta utilizada para firmar el token.
     */
    @NotBlank(message = "JWT secret must be configured")
    @Size(min = 32, message = "JWT secret must contain at least 32 characters")
    private String secret;

    /**
     * Tiempo de expiración en milisegundos.
     */
    private long expiration;

}
