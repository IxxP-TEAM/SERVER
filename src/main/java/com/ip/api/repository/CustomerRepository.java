package com.ip.api.repository;

import com.ip.api.domain.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer,Long> {

    List<Customer> findByCustomerNameContaining(String customerName);
    // 사업자등록번호 중복 여부 확인을 위한 쿼리 메서드
    boolean existsByRegistrationNumber(String registrationNumber);
    //페이징 처리
    Page<Customer> findByCustomerNameContaining(String customerName, Pageable pageable);
}
