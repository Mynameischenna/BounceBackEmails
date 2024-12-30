package com.example.email;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class BounceBackMonitor {

    private static final String EMAIL = "chennakesava13579@gmail.com";
    private static final String PASSWORD = "fbrm mefm tozj amnx"; // App Password
    private static final String IMAP_HOST = "imap.gmail.com";
    private static final String IMAP_PORT = "993";

    // Variable to store the UID of the last processed email
    private static String lastProcessedMessageId = null;

    public static void main(String[] args) {
        while (true) {
            monitorBounceBacks();
            try {
                Thread.sleep(30000); // Poll every 30 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void monitorBounceBacks() {
        Properties properties = new Properties();
        properties.put("mail.imap.host", IMAP_HOST);
        properties.put("mail.imap.port", IMAP_PORT);
        properties.put("mail.imap.ssl.enable", "true");

        Session session = Session.getInstance(properties);

        try {
            // Connect to IMAP server
            Store store = session.getStore("imap");
            store.connect(IMAP_HOST, EMAIL, PASSWORD);

            // Open INBOX
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            // Fetch all messages in the inbox
            Message[] messages = inbox.getMessages();

            // Process messages starting from the latest
            for (int i = messages.length - 1; i >= 0; i--) {
                MimeMessage mimeMessage = (MimeMessage) messages[i];

                // Get the unique message ID
                String messageId = mimeMessage.getMessageID();

                // Skip processing if the message has already been handled
                if (lastProcessedMessageId != null && lastProcessedMessageId.equals(messageId)) {
                    break;
                }

                // Check if it's a bounce-back email
                if (isBounceBack(mimeMessage)) {
                    System.out.println("Bounce-back email detected!");
                    System.out.println("Subject: " + mimeMessage.getSubject());
                    System.out.println("From: " + mimeMessage.getFrom()[0]);
                    System.out.println("Received Date: " + mimeMessage.getReceivedDate());


                    // Update the last processed message ID
                    lastProcessedMessageId = messageId;

                    // Process only the latest bounce-back email
                    break;
                }
            }

            inbox.close(false);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isBounceBack(MimeMessage message) {
        try {
            String fromAddress = message.getFrom()[0].toString().toLowerCase();
            return fromAddress.contains("mailer-daemon@googlemail.com");
        } catch (Exception e) {
            return false;
        }
    }

}
