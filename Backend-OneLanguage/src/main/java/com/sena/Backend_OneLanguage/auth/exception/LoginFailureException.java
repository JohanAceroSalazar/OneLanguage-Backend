package com.sena.Backend_OneLanguage.auth.exception;

import java.time.OffsetDateTime;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class LoginFailureException extends RuntimeException {

    private final HttpStatus status;
    private final String code;
    private final Integer remainingAttempts;
    private final OffsetDateTime lockedUntil;

    private LoginFailureException(
            HttpStatus status,
            String code,
            String message,
            Integer remainingAttempts,
            OffsetDateTime lockedUntil) {
        super(message);
        this.status = status;
        this.code = code;
        this.remainingAttempts = remainingAttempts;
        this.lockedUntil = lockedUntil;
    }

    public static LoginFailureException invalidCredentials(Integer remainingAttempts) {
        String message = remainingAttempts == null
                ? "Credenciales inv\u00e1lidas."
                : remainingAttempts == 1
                        ? "Contrase\u00f1a incorrecta. Te queda 1 intento."
                        : "Contrase\u00f1a incorrecta. Te quedan " + remainingAttempts + " intentos.";

        return new LoginFailureException(
                HttpStatus.UNAUTHORIZED,
                "INVALID_CREDENTIALS",
                message,
                remainingAttempts,
                null);
    }

    public static LoginFailureException temporarilyLocked(OffsetDateTime lockedUntil, String message) {
        return new LoginFailureException(
                HttpStatus.LOCKED,
                "ACCOUNT_TEMPORARILY_LOCKED",
                message,
                null,
                lockedUntil);
    }
}
