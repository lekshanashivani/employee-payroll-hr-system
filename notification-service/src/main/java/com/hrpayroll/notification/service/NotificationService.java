package com.hrpayroll.notification.service;

import com.hrpayroll.notification.entity.Notification;
import com.hrpayroll.notification.entity.NotificationType;
import com.hrpayroll.notification.entity.NotificationStatus;
import com.hrpayroll.notification.feign.EmployeeClient;
import com.hrpayroll.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Notification Service
 * 
 * Handles notification creation and email sending.
 * Non-blocking email sending.
 */
@Service
@Transactional
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmployeeClient employeeClient;

    public Notification createNotification(Long employeeId, String subject, String body, NotificationType type) {
        Notification notification = new Notification();
        notification.setEmployeeId(employeeId);
        notification.setSubject(subject);
        notification.setBody(body);
        notification.setType(type);
        notification.setStatus(NotificationStatus.PENDING);

        Notification saved = notificationRepository.save(notification);

        // Send email asynchronously (non-blocking)
        try {
            String employeeEmail = employeeClient.getEmployeeEmail(employeeId);
            if (employeeEmail != null) {
                emailService.sendEmail(saved, employeeEmail);
            }
        } catch (Exception e) {
            // Log but don't fail
            saved.setStatus(NotificationStatus.FAILED);
            saved.setErrorMessage(e.getMessage());
            notificationRepository.save(saved);
        }

        return saved;
    }

    public List<Notification> getNotificationsByEmployee(Long employeeId) {
        return notificationRepository.findByEmployeeId(employeeId);
    }

    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
    }
}

