package com.ip.api.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ip.api.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	
	// 제품이름 중복 체크
	boolean existsByProductName(String productName);
	
	List<Product> findByProductNameContaining(String productName, Pageable pageable);
	
}
