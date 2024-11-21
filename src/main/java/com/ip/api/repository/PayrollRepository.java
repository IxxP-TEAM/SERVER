package com.ip.api.repository;

import com.ip.api.domain.Payroll;
import com.ip.api.domain.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {
    @Query("SELECT p FROM Payroll p WHERE FUNCTION('YEAR', p.createdAt) = :year AND p.user = :user")
    Page<Payroll> findByCreateAtYearAndUser(@Param("year") int year, @Param("user") User user, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Payroll p SET p.paymentStatus = true WHERE p.paymentDate <= :today AND p.paymentStatus = false")
    void updatePaymentStatusForPastPayroll(LocalDate today);

    Page<Payroll> findByUserAndCreatedAtBetween(User user, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);
}
