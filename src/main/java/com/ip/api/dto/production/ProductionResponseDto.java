package com.ip.api.dto.production;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.ip.api.domain.Production;
import com.ip.api.domain.ProductionMaterial;
import com.ip.api.domain.enums.ProductionStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductionResponseDto {
	
	private Long productionId;
	private String productName; // 생산할 제품
	private LocalDate startDate; // 생산 시작 날짜
    private LocalDate endDate; // 생산 종료 날짜
    private int targetQuantity; // 생산 목표 수량
    private int resultQuantity;
    private ProductionStatus productionStatus; // 생산 상태
    private String userName; // 작성자 ID
    private List<ProductionMaterialResponseDto> materials;
    
    public static ProductionResponseDto from(Production production, List<ProductionMaterial> materials) {
        return ProductionResponseDto.builder()
        		.productionId(production.getProductionId())
                .productName(production.getProduct().getProductName())
                .startDate(production.getStartDate())
                .endDate(production.getEndDate())
                .targetQuantity(production.getTargetQuantity())
                .resultQuantity(production.getResultQuantity())
                .productionStatus(production.getProductionStatus())
                .userName(production.getUser().getUserName())
                .materials(materials.stream()
                        .map(ProductionMaterialResponseDto::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
