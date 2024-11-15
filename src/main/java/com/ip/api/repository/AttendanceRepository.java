package com.ip.api.repository;

import com.ip.api.domain.Attendence;
import com.ip.api.domain.User;
import com.ip.api.domain.enums.AttendanceStatus;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendence, Long> {
    Attendence findByUser(User user);
    Page<Attendence> findByUser(User user, Pageable pageable);
    Optional<Attendence> findByUserAndCheckInTimeBetween(User user, LocalDateTime startOfDay, LocalDateTime endOfDay);
    Attendence findByUserAndAttStatusAndCheckInTimeBetween(User user, AttendanceStatus attStatus, LocalDateTime checkInTime, LocalDateTime checkOutTime);
    boolean existsByUserAndAttStatusAndCheckInTimeBetween(User user, AttendanceStatus attStatus, LocalDateTime checkInTime, LocalDateTime checkOutTime);
}