package com.example.site.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

@Service
public class SendEmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(String to, String body, String topic){
        System.out.println ("sent email... 1");
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage ();
        simpleMailMessage.setFrom ("kundelikwarner@gmail.com");
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject (topic);
        simpleMailMessage.setText (body);

    javaMailSender.send (simpleMailMessage);
        System.out.println ("sent email... 2");
    }


}
