package vn.fastfood.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailVerificationService {

    private final JavaMailSender javaMailSender;

    public EmailVerificationService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendMail(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("onboard@resend.dev");
        message.setTo(email);
        message.setSubject("Xác thực tài khoản");
        message.setText("Mã xác thực: " + code);
        javaMailSender.send(message);
    }
}