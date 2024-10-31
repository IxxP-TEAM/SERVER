package com.ip.api.repository;

import com.ip.api.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer,Long> {

    List<Customer> findByCustomerNameContaining(String customerName);
}
