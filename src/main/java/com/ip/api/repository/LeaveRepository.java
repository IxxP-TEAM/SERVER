package com.ip.api.repository;

import com.ip.api.domain.Leaves;
import com.ip.api.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveRepository extends JpaRepository<Leaves, Long> {
    Page<Leaves> findByUser(Pageable pageable, User user);
}
