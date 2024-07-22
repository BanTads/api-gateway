package com.apigateway.auth.auth.services;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
@Service
public class EmailService {
    @Autowired
    private JavaMailSender emailSender;

    @Async
    public void sendEmail(String to, String subject, String text, String userType, String userName) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("bantadsemail@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);

        String logoUrl = "https://raw.githubusercontent.com/felipehbomfim/api-gateway/b5e2f377369eaea14b60632a75a6809f410c83b1/auth/src/main/java/com/apigateway/auth/auth/ImageTest/logo.png";  // URL pública para a imagem

        String messageStr = "<p>Seu usuário do BANTADS foi criado com sucesso.</p>";

        if(userType.equalsIgnoreCase("CLIENTE"))
            messageStr = "<p>Sua conta do BANTADS foi aprovada com sucesso.</p>";

        String htmlMsg = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "body {font-family: Arial, sans-serif;}" +
                ".container {max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #dcdcdc; border-radius: 10px; background-color: #f9f9f9;}" +
                ".header {background-color: #404649; color: white; padding: 10px 0; text-align: center; border-radius: 10px 10px 0 0;}" +
                ".content {padding: 20px;}" +
                ".footer {background-color: #f1f1f1; color: #555555; padding: 10px; text-align: center; border-radius: 0 0 10px 10px; font-size: 12px;}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<img src='" + logoUrl + "' alt='BANTADS Logo' width='200' height='auto'>" +  // Referência à imagem
                "</div>" +
                "<div class='content'>" +
                "<p>Olá, <strong>" + userName + "</strong></p>" +
                messageStr +
                "<p>Sua senha para acesso é: <strong>" + text + "</strong></p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>Este é um e-mail automático, por favor, não responda.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";

        helper.setText(htmlMsg, true);
        emailSender.send(message);
    }
}
