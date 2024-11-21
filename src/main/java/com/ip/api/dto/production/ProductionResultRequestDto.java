package com.ip.api.dto.production;

import com.ip.api.domain.enums.ProductionStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductionResultRequestDto {
	
	private Long productionId; // 생산 ID
    private int resultQuantity; // 생산된 제품 수량
    private ProductionStatus productionStatus; 
}


