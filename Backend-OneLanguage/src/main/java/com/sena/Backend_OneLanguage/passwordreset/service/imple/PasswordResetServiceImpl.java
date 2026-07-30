package com.sena.Backend_OneLanguage.passwordreset.service.imple;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sena.Backend_OneLanguage.email.service.EmailService;
import com.sena.Backend_OneLanguage.passwordreset.dto.ForgotPasswordRequest;
import com.sena.Backend_OneLanguage.passwordreset.dto.ResetPasswordRequest;
import com.sena.Backend_OneLanguage.passwordreset.entity.PasswordResetToken;
import com.sena.Backend_OneLanguage.passwordreset.repository.PasswordResetTokenRepository;
import com.sena.Backend_OneLanguage.passwordreset.service.PasswordResetService;
import com.sena.Backend_OneLanguage.users.entity.User;
import com.sena.Backend_OneLanguage.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetServiceImpl implements PasswordResetService {

    // URL del frontend donde el usuario podrá restablecer su contraseña.
    // Más adelante se puede mover al application.yml para no dejarla fija.
        private static final String RESET_PASSWORD_URL =
                "http://localhost:5173/reset-password";

    // Repositorio para consultar los usuarios.
        private final UserRepository userRepository;

    // Repositorio para gestionar los tokens de recuperación.
        private final PasswordResetTokenRepository passwordResetTokenRepository;

    // PasswordEncoder utilizado para hashear el token de recuperación.
        private final PasswordEncoder passwordEncoder;

        private final EmailService emailService;

        @Override
        @Transactional
        public void forgotPassword(ForgotPasswordRequest request) {

        // Buscar el usuario por correo.
        // Si no existe o fue eliminado lógicamente, se lanza una excepción.
        User user = userRepository
                .findByEmailAndDeletedAtIsNull(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("No existe un usuario con ese correo."));

        // Eliminar cualquier token anterior para que el usuario
        // solo tenga un enlace de recuperación válido.
        passwordResetTokenRepository.deleteAllByUser(user);

        // Generar un identificador único para el token.
        UUID tokenIdentifier = UUID.randomUUID();

        // Generar el token que se enviará al usuario.
        // Se unen dos UUID para hacerlo más largo y difícil de adivinar.
        String rawToken =
                UUID.randomUUID().toString() +
                UUID.randomUUID();

        // Hashear el token antes de almacenarlo en la base de datos.
        // Así el token real nunca queda guardado.
        String tokenHash =
                passwordEncoder.encode(rawToken);

        // Crear la entidad que representa el token de recuperación.
        PasswordResetToken token =
                PasswordResetToken.builder()
                        .user(user)
                        .tokenIdentifier(tokenIdentifier)
                        .tokenHash(tokenHash)

                        // El enlace será válido únicamente durante 15 minutos.
                        .expiresAt(
                                OffsetDateTime.now(ZoneOffset.UTC)
                                        .plusMinutes(15))
                        .build();

        // Guardar el token en la base de datos.
        passwordResetTokenRepository.save(token);

        // Construir el enlace que posteriormente será enviado por correo.
        String resetLink =
                RESET_PASSWORD_URL
                + "?id="
                + tokenIdentifier
                + "&token="
                + rawToken;

        // Enviar el enlace de recuperación al correo del usuario.
        emailService.sendPasswordResetEmail(
                user.getEmail(),
                user.getFullName(),
                resetLink
                );
        }

        @Override
        @Transactional
        public void resetPassword(ResetPasswordRequest request) {

        // 1. Verificar que las contraseñas coincidan
        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {

        throw new RuntimeException(
                "Las contraseñas no coinciden."
                );
        }

        // 2. Buscar el token por su identificador
        PasswordResetToken passwordResetToken =
                passwordResetTokenRepository
                .findByTokenIdentifier(
                request.getTokenIdentifier()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                        "El enlace de recuperación no es válido."
                        )
                );

        // 3. Verificar si el token ya fue utilizado
        if (passwordResetToken.getUsedAt() != null) {

        throw new RuntimeException(
                "El enlace de recuperación ya fue utilizado."
                );
        }

        // 4. Verificar si el token expiró
        if (passwordResetToken.getExpiresAt()
                .isBefore(OffsetDateTime.now(ZoneOffset.UTC))) {

        throw new RuntimeException(
                "El enlace de recuperación ha expirado."
                );
        }

        // 5. Verificar que el token recibido coincida
        boolean tokenValido =
                passwordEncoder.matches(
                request.getToken(),
                passwordResetToken.getTokenHash()
        );

        if (!tokenValido) {

        throw new RuntimeException(
                "El enlace de recuperación no es válido."
                );
        }

        // 6. Obtener el usuario asociado
        User user =
        passwordResetToken.getUser();

        // 7. Encriptar la nueva contraseña
        String passwordHash =
                passwordEncoder.encode(
                request.getNewPassword()
                );

        // 8. Actualizar la contraseña
        user.setPasswordHash(passwordHash);

        // 9. Guardar los cambios del usuario
        userRepository.save(user);

        // 10. Marcar el token como utilizado
        passwordResetToken.setUsedAt(
        OffsetDateTime.now(ZoneOffset.UTC)
        );

        passwordResetTokenRepository.save(
        passwordResetToken
        );

        log.info(
        "Contraseña restablecida correctamente para el usuario: ",
        user.getEmail()
                );
        }
}