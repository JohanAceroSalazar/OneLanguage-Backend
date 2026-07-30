package com.sena.Backend_OneLanguage.email.service.imple;

import com.sena.Backend_OneLanguage.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendPasswordResetEmail(
            String recipientEmail,
            String recipientName,
            String resetLink) {

        SimpleMailMessage message = new SimpleMailMessage();

        // Correo del usuario que recibirá el mensaje.
        message.setTo(recipientEmail);

        // Asunto del correo.
        message.setSubject("Recuperación de contraseña - One Language");

        // Contenido del correo.
        message.setText(
                "Hola " + recipientName + ",\n\n" +
                "Hemos recibido una solicitud para restablecer " +
                "la contraseña de tu cuenta de One Language.\n\n" +
                "Para crear una nueva contraseña, ingresa al siguiente enlace:\n\n" +
                resetLink + "\n\n" +
                "Este enlace es válido durante 15 minutos.\n\n" +
                "Si tú no solicitaste restablecer tu contraseña, " +
                "puedes ignorar este mensaje.\n\n" +
                "Saludos,\n" +
                "Equipo One Language"
        );

        // Envía el correo mediante el servidor SMTP configurado.
        mailSender.send(message);
    }
}