package com.sena.Backend_OneLanguage.email.service.imple;

import com.sena.Backend_OneLanguage.email.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendPasswordResetEmail(String recipientEmail, String recipientName, String resetLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
            helper.setTo(recipientEmail);
            helper.setSubject("Recuperación de contraseña - One Language");
            helper.setText(buildPasswordResetHtml(recipientName, resetLink), true);
            mailSender.send(message);
        } catch (MessagingException exception) {
            throw new MailPreparationException("No fue posible preparar el correo de recuperación.", exception);
        }
    }

    private String buildPasswordResetHtml(String recipientName, String resetLink) {
        String safeRecipientName = HtmlUtils.htmlEscape(
                recipientName == null || recipientName.isBlank() ? "" : recipientName.trim());
        String greeting = safeRecipientName.isEmpty() ? "Hola," : "Hola " + safeRecipientName + ",";
        String safeResetLink = HtmlUtils.htmlEscape(resetLink);

        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <meta name="x-apple-disable-message-reformatting">
                  <title>Recuperación de contraseña - One Language</title>
                  <style>
                    @media only screen and (max-width: 620px) {
                      .email-container { width: 100%% !important; }
                      .content-padding { padding: 32px 24px !important; }
                      .mobile-title { font-size: 25px !important; line-height: 32px !important; }
                      .mobile-button { display: block !important; width: 100%% !important; box-sizing: border-box !important; }
                    }
                  </style>
                </head>
                <body style="margin:0; padding:0; background-color:#F3F6FC;">
                  <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0" style="width:100%%; background-color:#F3F6FC; margin:0; padding:0;">
                    <tr><td align="center" style="padding:32px 16px;">
                      <table role="presentation" class="email-container" width="600" cellpadding="0" cellspacing="0" border="0" style="width:100%%; max-width:600px; margin:0 auto;">
                        <tr><td style="background-color:#2C77D0; padding:28px 32px; border-radius:16px 16px 0 0; font-family:Arial, Helvetica, sans-serif; color:#FFFFFF;">
                          <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0"><tr>
                            <td valign="middle" style="width:48px; padding-right:12px;"><table role="presentation" width="42" height="42" cellpadding="0" cellspacing="0" border="0" style="width:42px; height:42px; background-color:#FFFFFF; border-radius:21px;"><tr><td align="center" valign="middle" style="font-family:Arial, Helvetica, sans-serif; font-size:16px; font-weight:bold; color:#2C77D0;">OL</td></tr></table></td>
                            <td valign="middle"><div style="font-size:22px; line-height:27px; font-weight:bold;">One Language</div><div style="font-size:13px; line-height:19px; color:#EAF2FF;">Comunicación accesible para todos</div></td>
                          </tr></table>
                        </td></tr>
                        <tr><td class="content-padding" style="background-color:#FFFFFF; padding:40px 48px 36px; font-family:Arial, Helvetica, sans-serif; color:#183153; border-radius:0 0 16px 16px;">
                          <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0">
                            <tr><td align="center" style="padding-bottom:20px;"><table role="presentation" width="56" height="56" cellpadding="0" cellspacing="0" border="0" style="width:56px; height:56px; background-color:#EAF2FF; border:1px solid #BED7F5; border-radius:28px;"><tr><td align="center" valign="middle" style="font-size:28px; line-height:28px; color:#2C77D0;">&#128274;</td></tr></table></td></tr>
                            <tr><td class="mobile-title" align="center" style="font-size:28px; line-height:35px; font-weight:bold; color:#183153; padding-bottom:20px;">Restablece tu contraseña</td></tr>
                            <tr><td style="font-size:16px; line-height:25px; color:#183153; padding-bottom:16px;">%s</td></tr>
                            <tr><td style="font-size:16px; line-height:25px; color:#183153; padding-bottom:24px;">Hemos recibido una solicitud para restablecer la contraseña de tu cuenta de One Language.</td></tr>
                            <tr><td align="center" style="padding:4px 0 28px;"><a class="mobile-button" href="%s" target="_blank" style="display:inline-block; background-color:#2C77D0; border-radius:8px; color:#FFFFFF; font-family:Arial, Helvetica, sans-serif; font-size:16px; font-weight:bold; line-height:20px; padding:14px 24px; text-align:center; text-decoration:none;">Restablecer mi contraseña</a></td></tr>
                            <tr><td style="background-color:#F3F6FC; border-left:4px solid #2C77D0; border-radius:4px; color:#475569; font-size:14px; line-height:21px; padding:14px 16px;"><strong style="color:#183153;">Importante:</strong> este enlace es válido durante 15 minutos.</td></tr>
                            <tr><td style="font-size:14px; line-height:21px; color:#475569; padding-top:24px;">Si tú no solicitaste el cambio, puedes ignorar este correo. Tu contraseña actual permanecerá sin cambios.</td></tr>
                          </table>
                        </td></tr>
                        <tr><td align="center" style="font-family:Arial, Helvetica, sans-serif; color:#64748B; font-size:13px; line-height:20px; padding:24px 16px 0;"><strong style="color:#475569;">Equipo One Language</strong><br>Hacemos la comunicación más accesible.</td></tr>
                      </table>
                    </td></tr>
                  </table>
                </body>
                </html>
                """.formatted(greeting, safeResetLink);
    }
}
