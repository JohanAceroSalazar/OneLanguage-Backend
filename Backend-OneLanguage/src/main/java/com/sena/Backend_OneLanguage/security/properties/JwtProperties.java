package com.sena.Backend_OneLanguage.security.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * Clave secreta utilizada para firmar el token.
     */
    private String secret;

    /**
     * Tiempo de expiración en milisegundos.
     */
    private long expiration;

}