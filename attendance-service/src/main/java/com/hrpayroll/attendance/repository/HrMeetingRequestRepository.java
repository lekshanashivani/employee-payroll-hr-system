package com.hrpayroll.attendance.repository;

import com.hrpayroll.attendance.entity.HrMeetingRequest;
import com.hrpayroll.attendance.entity.MeetingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HrMeetingRequestRepository extends JpaRepository<HrMeetingRequest, Long> {
    List<HrMeetingRequest> findByEmployeeId(Long employeeId);
    List<HrMeetingRequest> findByStatus(MeetingStatus status);
}

