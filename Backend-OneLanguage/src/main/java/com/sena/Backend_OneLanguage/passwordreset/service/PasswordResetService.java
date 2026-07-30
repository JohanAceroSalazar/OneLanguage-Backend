package com.sena.Backend_OneLanguage.passwordreset.service;

import com.sena.Backend_OneLanguage.passwordreset.dto.ForgotPasswordRequest;
import com.sena.Backend_OneLanguage.passwordreset.dto.ResetPasswordRequest;

public interface PasswordResetService {

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

}