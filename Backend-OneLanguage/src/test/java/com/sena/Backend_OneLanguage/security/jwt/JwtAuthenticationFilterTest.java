package com.sena.Backend_OneLanguage.security.jwt;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sena.Backend_OneLanguage.security.model.CustomUserDetails;
import com.sena.Backend_OneLanguage.security.properties.JwtProperties;
import com.sena.Backend_OneLanguage.security.service.CustomUserDetailsService;
import com.sena.Backend_OneLanguage.users.entity.User;
import jakarta.servlet.FilterChain;
import java.security.SecureRandom;
import java.util.Base64;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

class JwtAuthenticationFilterTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authenticatesRequestWithValidJwt() throws Exception {
        JwtService jwtService = jwtServiceWithExpiration(60_000);
        User user = User.builder()
                .email("user@example.com")
                .passwordHash("encoded-password")
                .userStatus(true)
                .build();
        CustomUserDetails userDetails = new CustomUserDetails(user);
        CustomUserDetailsService userDetailsService = mock(CustomUserDetailsService.class);
        when(userDetailsService.loadUserByUsername(user.getEmail())).thenReturn(userDetails);

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
                jwtService,
                userDetailsService,
                new JwtAuthenticationEntryPoint());
        MockHttpServletRequest request = new MockHttpServletRequest("DELETE", "/api/users/1");
        request.addHeader("Authorization", "Bearer " + jwtService.generateToken(userDetails));
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        filter.doFilter(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void returnsUnauthorizedForExpiredJwt() throws Exception {
        JwtService jwtService = jwtServiceWithExpiration(-1_000);
        User user = User.builder()
                .email("user@example.com")
                .passwordHash("encoded-password")
                .userStatus(true)
                .build();
        CustomUserDetails userDetails = new CustomUserDetails(user);

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
                jwtService,
                mock(CustomUserDetailsService.class),
                new JwtAuthenticationEntryPoint());
        MockHttpServletRequest request = new MockHttpServletRequest("DELETE", "/api/users/1");
        request.addHeader("Authorization", "Bearer " + jwtService.generateToken(userDetails));
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, mock(FilterChain.class));

        assertEquals(401, response.getStatus());
    }

    private JwtService jwtServiceWithExpiration(long expiration) {
        byte[] secretBytes = new byte[48];
        new SecureRandom().nextBytes(secretBytes);

        JwtProperties properties = new JwtProperties();
        properties.setSecret(Base64.getEncoder().encodeToString(secretBytes));
        properties.setExpiration(expiration);
        return new JwtService(properties);
    }
}
