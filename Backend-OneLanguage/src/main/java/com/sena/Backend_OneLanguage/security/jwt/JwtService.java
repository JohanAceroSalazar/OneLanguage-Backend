package com.sena.Backend_OneLanguage.security.jwt;

import com.sena.Backend_OneLanguage.security.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    /**
     * Genera un JWT utilizando el email del usuario.
     */
    public String generateToken(UserDetails userDetails) {

        return generateToken(new HashMap<>(), userDetails);

    }

    /**
     * Genera un JWT con claims adicionales.
     */
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails) {

        Date now = new Date();

        Date expiration = new Date(
                now.getTime() + jwtProperties.getExpiration());

        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Obtiene el email almacenado en el token.
     */
    public String extractUsername(String token) {

        return extractClaim(token, Claims::getSubject);

    }

    /**
     * Obtiene la fecha de expiración.
     */
    public Date extractExpiration(String token) {

        return extractClaim(token, Claims::getExpiration);

    }

    /**
     * Obtiene cualquier claim.
     */
    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver) {

        Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);

    }

    /**
     * Valida si el token pertenece al usuario.
     */
    public boolean isTokenValid(
            String token,
            UserDetails userDetails) {

        String username = extractUsername(token);

        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);

    }

    /**
     * Verifica si expiró.
     */
    private boolean isTokenExpired(String token) {

        return extractExpiration(token)
                .before(new Date());

    }

    /**
     * Lee todos los claims.
     */
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }

    /**
     * Convierte la clave secreta en SecretKey.
     */
    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
            jwtProperties.getSecret().getBytes()
        );
    }
}