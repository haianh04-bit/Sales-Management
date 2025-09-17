package com.codegym.services;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendMail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            helper.setFrom("phamhaianhpc10@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // false = plain text, true = HTML

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi gửi email: " + e.getMessage(), e);
        }
    }
}
