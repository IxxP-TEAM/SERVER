package com.ip.api.repository;

import com.ip.api.domain.Leaves;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveRepository extends JpaRepository<Leaves, Long> {
}