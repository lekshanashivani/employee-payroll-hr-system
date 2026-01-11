package com.hrpayroll.notification.service;

import com.hrpayroll.notification.dto.AuditLogRequest;
import com.hrpayroll.notification.entity.Announcement;
import com.hrpayroll.notification.entity.TargetAudience;
import com.hrpayroll.notification.feign.AuditLogClient;
import com.hrpayroll.notification.feign.EmployeeClient;
import com.hrpayroll.notification.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Announcement Service
 * 
 * Handles HR announcements.
 * Broadcast to ALL / HR / EMPLOYEE.
 * Optional email flag.
 */
@Service
@Transactional
public class AnnouncementService {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmployeeClient employeeClient;

    @Autowired
    private AuditLogClient auditLogClient;

    public Announcement createAnnouncement(Announcement announcement, Long createdByUserId) {
        announcement.setCreatedBy(createdByUserId);
        Announcement saved = announcementRepository.save(announcement);

        // Send emails if flag is set
        if (announcement.getSendEmail()) {
            try {
                List<Long> targetEmployeeIds = employeeClient
                        .getEmployeeIdsByAudience(announcement.getTargetAudience().name());
                for (Long employeeId : targetEmployeeIds) {
                    notificationService.createNotification(
                            employeeId,
                            announcement.getTitle(),
                            announcement.getContent(),
                            com.hrpayroll.notification.entity.NotificationType.ANNOUNCEMENT);
                }
            } catch (Exception e) {
                // Log but don't fail
            }
        }

        // Audit announcement creation
        try {
            Map<String, Object> newValues = new HashMap<>();
            newValues.put("title", announcement.getTitle());
            newValues.put("targetAudience", announcement.getTargetAudience().name());

            AuditLogRequest auditRequest = new AuditLogRequest();
            auditRequest.setAction("ANNOUNCEMENT_CREATED");
            auditRequest.setServiceName("Notification Service");
            auditRequest.setPerformedBy(createdByUserId);
            auditRequest.setTargetId(saved.getId());
            auditRequest.setDescription("Announcement created: " + announcement.getTitle());
            auditRequest.setOldValues(null);
            auditRequest.setNewValues(newValues);
            auditLogClient.createAuditLog(auditRequest);
        } catch (Exception e) {
            // Non-blocking audit
        }

        return saved;
    }

    public List<Announcement> getActiveAnnouncements() {
        return announcementRepository.findActiveAnnouncements(LocalDateTime.now());
    }

    public Announcement getAnnouncementById(Long id) {
        return announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));
    }

    public void deleteAnnouncement(Long id) {
        announcementRepository.deleteById(id);
    }
}
