package com.zinikai.shop.domain.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class MailService {

    private final JavaMailSender mailSender;


    public void sendWelcomeEmail(String toEmail, String username) {
        String subject = "会員登録が完了しました";
        String content = "<h1>会員登録ありがとうござい！、, " + username + "様!</h1>"
                + "こちらのページからログインいただけます。";

        sendEmail(toEmail, subject, content);
    }

    public void sendPaymentCompletedEmail(String toEmail, String username, String orderUuid, BigDecimal price, String paymentMethod) {
        String subject = "ZINIショップのお支払い完了しました。";
        String content = "<h1>" + username + "様、お支払い完了しました</h1>"
                + "<p>注文番号: " + orderUuid + "</p>"
                + "<p>注文金額: " + price + "円</p>"
                + "<p>注文方法: " + paymentMethod + "</p>";

        sendEmail(toEmail, subject, content);
    }


    private void sendEmail(String toEmail, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content, true);  // HTML 적용
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}

