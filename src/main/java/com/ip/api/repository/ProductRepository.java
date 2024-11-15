package com.ip.api.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ip.api.domain.Product;
import com.ip.api.domain.User;

public interface ProductRepository extends JpaRepository<Product, Long> {
	
	// 제품이름 중복 체크
	boolean existsByProductName(String productName);
	
	// 특정 ID를 제외하고 제품 이름 중복 체크
    boolean existsByProductNameAndProductIdNot(String productName, Long id);
	
	Page<Product> findByProductNameContaining(String productName, Pageable pageable);

	Optional<Product> findByProductName(String productName);
	
}
