package com.ip.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ip.api.domain.ProductionMaterial;

public interface ProductionMaterialRepository extends JpaRepository<ProductionMaterial, Long> {

	List<ProductionMaterial> findByProduction_ProductionId(Long productionId);

	
}
