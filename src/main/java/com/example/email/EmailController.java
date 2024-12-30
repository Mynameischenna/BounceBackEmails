package com.example.email;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.web.bind.annotation.*;
import java.util.Properties;

@RestController
public class EmailController {

    private static final String EMAIL = "chennakesava13579@gmail.com";
    private static final String PASSWORD = "fbrm mefm tozj amnx";

    @PostMapping("/mail")
    public String sendEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String body) {

        String host = "smtp.gmail.com";

        // Set system properties
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Authenticate the sender's email
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL, PASSWORD);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            return "Email sent successfully to " + to + "!";
        } catch (MessagingException mex) {
            return "Error: Unable to send email.";
        }
    }
}
