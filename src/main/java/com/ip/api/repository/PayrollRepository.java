package com.ip.api.repository;

import com.ip.api.domain.Payroll;
import com.ip.api.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {
    @Query("SELECT p FROM Payroll p WHERE FUNCTION('YEAR', p.createdAt) = :year AND p.user = :user")
    Page<Payroll> findByCreateAtYearAndUser(@Param("year") int year, @Param("user") User user, Pageable pageable);
}
