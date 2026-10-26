package com.sena.Backend_OneLanguage.email.service;

public interface EmailService {

    void sendPasswordResetEmail(
            String recipientEmail,
            String recipientName,
            String resetLink
    );

}