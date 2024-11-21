package com.ip.api.dto.production;

import java.time.LocalDate;

import com.ip.api.domain.Production;
import com.ip.api.domain.enums.ProductionStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductionListResponseDto {
	// 생산계획 리스트 (원재료 제외)
	

	private Long productionId;
	private String productName; // 생산할 제품
	private LocalDate startDate; // 생산 시작 날짜
    private LocalDate endDate; // 생산 종료 날짜
    private int targetQuantity; // 생산 목표 수량
    private int resultQuantity;
    private ProductionStatus productionStatus; // 생산 상태
    private String userName;
    
    public static ProductionListResponseDto from(Production production) {
        return ProductionListResponseDto.builder()
        		.productionId(production.getProductionId())
                .productName(production.getProduct().getProductName())
                .startDate(production.getStartDate())
                .endDate(production.getEndDate())
                .targetQuantity(production.getTargetQuantity())
                .resultQuantity(production.getResultQuantity())
                .productionStatus(production.getProductionStatus())
                .userName(production.getUser().getUserName())
                .build();
    }
}
