package com.ip.api.repository;

import com.ip.api.domain.Attendence;
import com.ip.api.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendence, Long> {
    Attendence findByUser(User user);
}
