package com.ip.api.dto.production;

import com.ip.api.domain.ProductionMaterial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductionMaterialResponseDto {
	
	private Long materialId;
	private Long productionId;
	private String productMaterialName; // 원재료 (product_id)
	private int materialQuantity;
	
	
	
	public static ProductionMaterialResponseDto from(ProductionMaterial material) {
        return ProductionMaterialResponseDto.builder()
                .materialId(material.getMaterialId()) // 원재료 ID 매핑
                .productionId(material.getProduction().getProductionId()) // 생산 계획 ID 매핑
                .productMaterialName(material.getProduct().getProductName()) // 원재료 이름 매핑
                .materialQuantity(material.getConsumedQuantity()) // 소모된 수량 매핑
                .build();
    }
	
}
