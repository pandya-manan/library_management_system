package com.oops.library.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String senderAddress;
    private final String baseUrl;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    public EmailService(JavaMailSender mailSender,
                        @Value("${spring.mail.username:}") String senderAddress,
                        @Value("${app.base-url:http://localhost:9300}") String baseUrl) {
        this.mailSender = mailSender;
        this.senderAddress = senderAddress;
        this.baseUrl = baseUrl;
        log.info("EmailService initialized with sender: {}, baseUrl: {}", senderAddress, baseUrl);
    }

    public boolean sendPasswordResetOtp(String recipient, String otp) {
        return sendSimpleMessage(recipient,
                "Enchanted Library Password Reset OTP",
                "Your one-time password to reset your Enchanted Library account is "
                        + otp + ".\n\nThis code expires in 10 minutes."
                        + "\n\nUse this link to reset your password: "
                        + buildResetLink(recipient)
                        + "\nEnter the OTP along with your new password." 
                        + "\n\nIf you did not request a reset, you can safely ignore this message.");
    }

    public boolean sendSignupConfirmation(String recipient, String name) {
        return sendSimpleMessage(recipient,
                "Welcome to the Enchanted Library",
                "Hello " + name + ",\n\nYour Enchanted Library account is now active."
                        + "\nStart exploring magical books and resources today!\n\nHappy Reading,\nEnchanted Library Team");
    }

    public boolean sendLoginNotification(String recipient, String name) {
        return sendSimpleMessage(recipient,
                "Login Alert",
                "Hi " + name + ",\n\nYour Enchanted Library account was just accessed."
                        + "\nIf this wasn't you, please reset your password immediately.\n\nRegards,\nEnchanted Library Security");
    }

    public boolean sendBorrowConfirmation(String recipient, String name, String bookTitle, LocalDate dueDate) {
        return sendSimpleMessage(recipient,
                "Book Borrowed Successfully",
                "Hi " + name + ",\n\nYou have borrowed '" + bookTitle + "'."
                        + "\nPlease return it by " + DATE_FORMAT.format(dueDate) + "."
                        + "\n\nHappy Reading!\nEnchanted Library Team");
    }

    public boolean sendReturnConfirmation(String recipient, String name, String bookTitle) {
        return sendSimpleMessage(recipient,
                "Book Return Confirmation",
                "Hi " + name + ",\n\nThank you for returning '" + bookTitle + "'."
                        + "\nWe hope you enjoyed it!\n\nSee you again soon.\nEnchanted Library Team");
    }

    public void sendOverdueSummary(List<String> librarianEmails, String summaryBody) {
        if (librarianEmails == null || librarianEmails.isEmpty()) {
            log.warn("Attempted to send overdue summary to empty email list");
            return;
        }

        int successCount = 0;
        int failureCount = 0;

        for (String email : librarianEmails) {
            boolean sent = sendSimpleMessage(email,
                    "Daily Overdue Report",
                    summaryBody);
            if (sent) {
                successCount++;
            } else {
                failureCount++;
            }
        }

        log.info("Overdue summary sent: {} successful, {} failed", successCount, failureCount);
    }

    private boolean sendSimpleMessage(String recipient, String subject, String body) {
        try {
            // Validate recipient
            if (recipient == null || recipient.isBlank()) {
                log.error("Attempted to send email with invalid recipient: '{}'", recipient);
                return false;
            }

            // Validate recipient email format
            if (!isValidEmail(recipient)) {
                log.error("Invalid email format for recipient: '{}'", recipient);
                return false;
            }

            // Validate mailSender
            if (mailSender == null) {
                log.error("JavaMailSender is not initialized - cannot send email to: {}", recipient);
                return false;
            }

            log.debug("Preparing to send email to: {} with subject: '{}'", recipient, subject);

            SimpleMailMessage message = new SimpleMailMessage();
            
            // Set sender address
            if (senderAddress != null && !senderAddress.isBlank() && isValidEmail(senderAddress)) {
                message.setFrom(senderAddress);
            } else {
                log.warn("No valid sender address configured, using default");
                // Let Spring use the default from address
            }
            
            message.setTo(recipient.trim());
            message.setSubject(subject);
            message.setText(body);
            
            log.info("Sending email to: {} with subject: '{}'", recipient, subject);
            mailSender.send(message);
            log.info("Successfully sent email to: {} with subject: '{}'", recipient, subject);
            return true;
            
        } catch (MailException ex) {
            log.error("MailException sending email to {} with subject '{}': {}", recipient, subject, ex.getMessage(), ex);
            return false;
        } catch (Exception ex) {
            log.error("Unexpected error sending email to {} with subject '{}': {}", recipient, subject, ex.getMessage(), ex);
            return false;
        }
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        // Basic email validation
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    private String buildResetLink(String email) {
        try {
            String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
            return baseUrl + "/reset-password?email=" + encodedEmail;
        } catch (Exception e) {
            log.error("Error building reset link for email: {}", email, e);
            return baseUrl + "/reset-password";
        }
    }

    // Optional: Add a method to check email service health
    public boolean isHealthy() {
        try {
            // Try to create a test message to verify mail sender is working
            if (mailSender == null) {
                return false;
            }
            // Some JavaMailSender implementations allow testing connection
            // This is a basic check - you might need to adapt based on your mail provider
            return true;
        } catch (Exception e) {
            log.warn("Email service health check failed: {}", e.getMessage());
            return false;
        }
    }
}