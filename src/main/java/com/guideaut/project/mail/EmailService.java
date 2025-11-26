package com.guideaut.project.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:no-reply@guideaut.com}")
    private String defaultFrom;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envia um código numérico de 6 dígitos para redefinição de senha.
     */
    public void sendPasswordResetCodeEmail(String to, String code) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setFrom(defaultFrom);
        msg.setSubject("GuideAut - Código para redefinição de senha");
        msg.setText("""
                Olá,

                Recebemos uma solicitação para redefinir a senha da sua conta no GuideAut.

                Seu código de verificação é: %s

                Ele é válido por 1 hora. Não o compartilhe com ninguém.

                Se você não fez essa solicitação, pode ignorar este e-mail.

                Atenciosamente,
                Equipe GuideAut
                """.formatted(code));

        mailSender.send(msg);
    }
}
