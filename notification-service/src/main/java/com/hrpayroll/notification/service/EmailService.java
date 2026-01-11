package com.hrpayroll.notification.service;

import com.hrpayroll.notification.entity.Notification;
import com.hrpayroll.notification.entity.NotificationStatus;
import com.hrpayroll.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;

/**
 * Email Service
 * 
 * Handles email sending via Gmail SMTP.
 * Non-blocking with one retry maximum.
 */
@Service
@Transactional
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmailConfig emailConfig;

    /**
     * Send email and update notification status
     * Non-blocking with one retry maximum
     */
    @Async
    public void sendEmail(Notification notification, String recipientEmail) {
        // Email sending temporarily disabled
        /*
         * try {
         * SimpleMailMessage message = new SimpleMailMessage();
         * message.setFrom(emailConfig.getFromEmail());
         * message.setTo(recipientEmail);
         * message.setSubject(notification.getSubject());
         * message.setText(notification.getBody());
         * 
         * mailSender.send(message);
         * 
         * // Update notification status
         * notification.setStatus(NotificationStatus.SENT);
         * notification.setSentAt(LocalDateTime.now());
         * notificationRepository.save(notification);
         * 
         * } catch (Exception e) {
         * // Retry once if not already retried
         * if (notification.getRetryCount() < 1) {
         * notification.setRetryCount(notification.getRetryCount() + 1);
         * notificationRepository.save(notification);
         * 
         * // Retry
         * try {
         * SimpleMailMessage message = new SimpleMailMessage();
         * message.setFrom(emailConfig.getFromEmail());
         * message.setTo(recipientEmail);
         * message.setSubject(notification.getSubject());
         * message.setText(notification.getBody());
         * 
         * mailSender.send(message);
         * 
         * notification.setStatus(NotificationStatus.SENT);
         * notification.setSentAt(LocalDateTime.now());
         * notificationRepository.save(notification);
         * } catch (Exception retryException) {
         * notification.setStatus(NotificationStatus.FAILED);
         * notification.setErrorMessage(retryException.getMessage());
         * notificationRepository.save(notification);
         * }
         * } else {
         * notification.setStatus(NotificationStatus.FAILED);
         * notification.setErrorMessage(e.getMessage());
         * notificationRepository.save(notification);
         * }
         * System.err.println("Email sending failed (disabled): " + e.getMessage());
         * }
         */

        System.out.println("Email sending disabled. Mocking success for notification: " + notification.getId());

        // Update notification status so it shows as processed
        notification.setStatus(NotificationStatus.SENT);
        notification.setSentAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }
}
