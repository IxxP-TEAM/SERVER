package com.ip.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ip.api.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
