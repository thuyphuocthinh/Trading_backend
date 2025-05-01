package com.tpt.trading.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {
    private final JavaMailSender javaMailSender;

    public EmailService() {
        this.javaMailSender = new JavaMailSenderImpl();
    }

    public void sendVerificationOtpEmail(String email, String otp)  {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");
        String subject = "VERIFY OTP";
        String text = "Your verification code is " + otp;
        try {
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(text);
            mimeMessageHelper.setTo(email);
        } catch (MessagingException e) {
            log.error("Send mail error: {}", e.getMessage());
        }
    }
}
