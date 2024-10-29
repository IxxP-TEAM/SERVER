package com.ip.api.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username")
    private String senderEmail;

    public void sendEmail(String connId, String userEmail, String username) throws MessagingException {
        String subject = "IxxP 사원 아이디 안내 메일입니다.";
        String body = "<p>안녕하세요. " + username + "님,</p>"
                + "<p>아이디: " + connId + " 입니다. </p>";

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(senderEmail);
        helper.setTo(userEmail);
        helper.setSubject(subject);
        helper.setText(body, true);

        javaMailSender.send(message);
    }
}
