package com.masferrer.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class EmailUtil {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.from}")
    private String fromEmail;

    public void sendSetPasswordEmail(String email) throws MessagingException{
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Set your password");
        mimeMessageHelper.setText("""
                <div>
                <a href="http://localhost:8080/api/user/set-password?email=%s" target="_blank">Set your password</a>
                </div>
                """.formatted(email), true);
        javaMailSender.send(mimeMessage);
    }

    public void sendVerificationCodeEmail(String verifiedEmail, String code, String userName, String userEmail) throws MessagingException{
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(verifiedEmail);
        mimeMessageHelper.setSubject("Codigo de verificación");
        mimeMessageHelper.setFrom(fromEmail);
        mimeMessageHelper.setText("""
                <div>
                    <p>El usuario con nombre <b>%s</b> y correo <b>%s</b> ha solicitado el cambio de contraseña, su código de verificación es: <b>%s</b></p>
                </div>
                """.formatted(userName, userEmail, code), true);
        javaMailSender.send(mimeMessage);
                
    }
}
