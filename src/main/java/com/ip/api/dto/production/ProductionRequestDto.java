package com.ip.api.dto.production;

import java.time.LocalDate;
import java.util.List;

import com.ip.api.domain.enums.ProductionStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductionRequestDto {
	
	private String productName; // 생산할 제품 ID (product_id)
    private LocalDate startDate; // 생산 시작 날짜
    private LocalDate endDate; // 생산 종료 날짜
    private int targetQuantity; // 생산 목표 수량
    private ProductionStatus productionStatus; // 생산 상태
    private Long userId; // 작성자 ID
    
    private List<ProductionMaterialRequestDto> materials; // 원재료 리스트

}
