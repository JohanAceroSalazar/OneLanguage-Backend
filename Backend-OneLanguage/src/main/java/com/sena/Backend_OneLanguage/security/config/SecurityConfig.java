package com.sena.Backend_OneLanguage.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.sena.Backend_OneLanguage.security.jwt.JwtAuthenticationEntryPoint;
import com.sena.Backend_OneLanguage.security.jwt.JwtAuthenticationFilter;
import org.springframework.http.HttpMethod;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        private final JwtAuthenticationEntryPoint authenticationEntryPoint;

        @Bean
        public SecurityFilterChain securityFilterChain(
        HttpSecurity http)
        throws Exception {

        http

                .csrf(csrf -> csrf.disable())

                .cors(Customizer.withDefaults())

                .sessionManagement(session ->

                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                .exceptionHandling(exception ->

                        exception.authenticationEntryPoint(
                                authenticationEntryPoint))

                .authorizeHttpRequests(auth -> auth

                        // Endpoint de error de Spring Boot
                        .requestMatchers("/error")
                        .permitAll()

                        // Swagger
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Login
                        .requestMatchers(HttpMethod.POST, "/auth/login")
                        .permitAll()

                        // Registro de usuarios
                        .requestMatchers(HttpMethod.POST, "/api/users")
                        .permitAll()

                        // (Opcional) consultar usuarios sin autenticación
                        .requestMatchers(HttpMethod.GET, "/api/users/**")
                        .permitAll()

                        // Password Reset
                        .requestMatchers(
                                HttpMethod.POST,
                                "/auth/forgot-password",
                                "/auth/reset-password"
                        ).permitAll()

                        // Todo lo demás requiere JWT
                        .anyRequest()
                        .authenticated())

                .addFilterBefore(

                        jwtAuthenticationFilter,

                        UsernamePasswordAuthenticationFilter.class);

        return http.build();

        }

        @Bean
        public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();

        }

        @Bean
        public AuthenticationManager authenticationManager(

                AuthenticationConfiguration configuration)

                throws Exception {

        return configuration.getAuthenticationManager();
        }
}