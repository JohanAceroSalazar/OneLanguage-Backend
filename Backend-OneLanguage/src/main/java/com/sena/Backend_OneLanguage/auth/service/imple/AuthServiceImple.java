package com.sena.Backend_OneLanguage.auth.service.imple;

import com.sena.Backend_OneLanguage.auth.dto.AuthResponseDto;
import com.sena.Backend_OneLanguage.auth.dto.LoginRequestDto;
import com.sena.Backend_OneLanguage.auth.exception.LoginFailureException;
import com.sena.Backend_OneLanguage.auth.mapper.AuthMapper;
import com.sena.Backend_OneLanguage.auth.service.AuthService;
import com.sena.Backend_OneLanguage.security.jwt.JwtService;
import com.sena.Backend_OneLanguage.security.model.CustomUserDetails;
import com.sena.Backend_OneLanguage.users.dto.UserResponseDto;
import com.sena.Backend_OneLanguage.users.entity.User;
import com.sena.Backend_OneLanguage.users.mapper.UserMapper;
import com.sena.Backend_OneLanguage.users.repository.UserRepository;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class AuthServiceImple implements AuthService {

        private static final int MAX_FAILED_ATTEMPTS = 5;
        private static final Duration LOCK_DURATION = Duration.ofMinutes(3);

        private final JwtService jwtService;
        private final UserMapper userMapper;
        private final AuthMapper authMapper;
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;

        @Override
        @Transactional(noRollbackFor = LoginFailureException.class)
        public AuthResponseDto login(LoginRequestDto request) {
        User user = userRepository.findForLoginByEmailAndDeletedAtIsNull(request.getEmail())
                .orElseThrow(() -> LoginFailureException.invalidCredentials(null));

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime lockedUntil = user.getLockedUntil();

        if (lockedUntil != null && lockedUntil.isAfter(now)) {
                throw LoginFailureException.temporarilyLocked(
                        lockedUntil,
                        lockedMessage(lockedUntil, now));
        }

        if (lockedUntil != null && !lockedUntil.isAfter(now)) {
                user.setFailedAttempts(0);
                user.setLockedUntil(null);
        }

        if (!Boolean.TRUE.equals(user.getUserStatus())) {
                throw LoginFailureException.invalidCredentials(null);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                int failedAttempts = Math.max(0, user.getFailedAttempts() == null ? 0 : user.getFailedAttempts()) + 1;
                user.setFailedAttempts(failedAttempts);

                if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                        OffsetDateTime newLockedUntil = now.plus(LOCK_DURATION);
                        user.setLockedUntil(newLockedUntil);
                        userRepository.save(user);
                        throw LoginFailureException.temporarilyLocked(
                                newLockedUntil,
                                "Tu cuenta ha sido bloqueada temporalmente por varios intentos fallidos. "
                                        + "Intenta nuevamente en 3 minutos.");
                }

                userRepository.save(user);
                throw LoginFailureException.invalidCredentials(MAX_FAILED_ATTEMPTS - failedAttempts);
        }

        user.setFailedAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        String token = jwtService.generateToken(userDetails);

        UserResponseDto responseDto =
                userMapper.toResponse(user);

        return authMapper.toResponse(
                token,
                responseDto);
        }

        private String lockedMessage(OffsetDateTime lockedUntil, OffsetDateTime now) {
                long minutes = Math.max(1, Duration.between(now, lockedUntil).toMinutes());
                String unit = minutes == 1 ? "minuto" : "minutos";
                return "Tu cuenta est\u00e1 temporalmente bloqueada. Intenta nuevamente en aproximadamente "
                        + minutes + " " + unit + ".";
        }
}
