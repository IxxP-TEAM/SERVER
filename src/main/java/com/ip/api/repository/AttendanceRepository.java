package com.ip.api.repository;

import com.ip.api.domain.Attendence;
import com.ip.api.domain.User;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendence, Long> {
    Attendence findByUser(User user);
    Optional<Attendence> findByUserAndCheckInTimeBetween(User user, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
