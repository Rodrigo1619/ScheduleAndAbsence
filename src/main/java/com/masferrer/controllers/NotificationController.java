package com.masferrer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.masferrer.models.dtos.EmailDTO;

@RestController
@RequestMapping("api/email")
@CrossOrigin("*")
public class NotificationController {
    @Autowired
    private JavaMailSender javaMailSender;

    @PostMapping("/send-email")
    public String sendEmail(@RequestBody EmailDTO emailDto) {
        //llenando el dto con los datos del email
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(emailDto.getTo());
        simpleMailMessage.setSubject(emailDto.getSubject());
        simpleMailMessage.setText(emailDto.getText());

        javaMailSender.send(simpleMailMessage);

        return "Email sent succesfully";
    }
}
