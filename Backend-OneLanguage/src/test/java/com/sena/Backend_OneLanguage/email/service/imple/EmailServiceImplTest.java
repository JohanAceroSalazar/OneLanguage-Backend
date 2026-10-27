package com.sena.Backend_OneLanguage.email.service.imple;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Test
    void sendsEscapedResponsiveHtmlPasswordResetEmail() throws Exception {
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        EmailServiceImpl emailService = new EmailServiceImpl(mailSender);

        emailService.sendPasswordResetEmail(
                "recipient@example.com",
                "<Ana & Luis>",
                "https://one-language.example/reset-password?id=123&token=secret-token");

        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        MimeMessage sentMessage = messageCaptor.getValue();
        sentMessage.saveChanges();
        String html = (String) sentMessage.getContent();
        assertEquals("Recuperación de contraseña - One Language", sentMessage.getSubject());
        assertTrue(sentMessage.getContentType().startsWith("text/html"));
        assertTrue(html.contains("Hola &lt;Ana &amp; Luis&gt;,"));
        assertTrue(html.contains("Hemos recibido una solicitud para restablecer la contraseña de tu cuenta de One Language."));
        assertTrue(html.contains("href=\"https://one-language.example/reset-password?id=123&amp;token=secret-token\""));
        assertTrue(html.contains("@media only screen and (max-width: 620px)"));
        assertTrue(html.contains("este enlace es válido durante 15 minutos."));
        assertFalse(html.contains("<Ana & Luis>"));
    }

    @Test
    void usesGenericGreetingWhenRecipientNameIsMissing() throws Exception {
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        EmailServiceImpl emailService = new EmailServiceImpl(mailSender);

        emailService.sendPasswordResetEmail("recipient@example.com", " ", "https://one-language.example/reset");

        assertTrue(((String) mimeMessage.getContent()).contains("Hola,"));
    }
}
