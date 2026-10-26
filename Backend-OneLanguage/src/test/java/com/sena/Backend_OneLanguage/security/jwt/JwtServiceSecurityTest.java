package com.sena.Backend_OneLanguage.security.jwt;

import com.sena.Backend_OneLanguage.security.properties.JwtProperties;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceSecurityTest {
    @Test
    void rejectsTokenSignedWithDifferentKey() {
        UserDetails user = User.withUsername("security-test@example.com").password("unused").authorities("USER").build();
        String token = service("first-test-only-jwt-secret-with-at-least-32-chars").generateToken(user);
        assertThrows(JwtException.class, () -> service("second-test-only-jwt-secret-with-at-least-32chars").extractUsername(token));
    }

    private JwtService service(String secret) {
        JwtProperties properties = new JwtProperties();
        properties.setSecret(secret);
        properties.setExpiration(60_000);
        return new JwtService(properties);
    }
}
