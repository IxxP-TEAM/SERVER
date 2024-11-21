package com.ip.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ip.api.domain.Production;
import com.ip.api.domain.ProductionAnalysis;

public interface ProductionAnalysisRepository extends JpaRepository<ProductionAnalysis, Long> {

	ProductionAnalysis findByProduction_ProductionId(Long productionId);

	boolean existsByProduction(Production production);

}
