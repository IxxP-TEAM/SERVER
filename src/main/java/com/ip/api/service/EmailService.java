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

    @Value("${spring.mail.username}")
    private String senderEmail;

    // 이메일 본문만 다르게 받아서 처리하는 일반화된 sendEmail 메서드
    public void sendEmail(String userEmail, String subject, String body) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(senderEmail);
        helper.setTo(userEmail);
        helper.setSubject(subject);
        helper.setText(body, true);

        javaMailSender.send(message);
    }

    // 아이디 안내 이메일 보내는 메서드
    public void sendIdNotificationEmail(String connId, String userEmail, String username) throws MessagingException {
        String subject = "IxxP 사원 아이디 안내 메일입니다.";
        String body = "<p>안녕하세요. " + username + "님,</p>"
                + "<p>아이디: " + connId + " 입니다. </p>"
                + "<p>초기 비밀번호는 0000 입니다. 로그인 후 마이페이지에서 비밀번호 재설정을 진행해주세요. </p>";

        sendEmail(userEmail, subject, body);
    }

    // 비밀번호 재설정 코드 이메일 보내는 메서드
    public void sendPasswordResetCodeEmail(String resetCode, String userEmail, String username) throws MessagingException {
        String subject = "IxxP 비밀번호 재설정 코드 메일입니다.";
        String body = "<p>안녕하세요. " + username + "님,</p>"
                + "<p>비밀번호 재설정을 위한 코드: <strong>" + resetCode + "</strong> 입니다.</p>"
                + "<p>해당 코드를 입력하여 비밀번호를 재설정해주세요.</p>";

        sendEmail(userEmail, subject, body);
    }
}
