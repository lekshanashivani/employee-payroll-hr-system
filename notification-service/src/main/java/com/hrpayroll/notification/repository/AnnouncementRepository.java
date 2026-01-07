package com.hrpayroll.notification.repository;

import com.hrpayroll.notification.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    @Query("SELECT a FROM Announcement a WHERE (a.expiresAt IS NULL OR a.expiresAt > :now)")
    List<Announcement> findActiveAnnouncements(@Param("now") LocalDateTime now);
}

