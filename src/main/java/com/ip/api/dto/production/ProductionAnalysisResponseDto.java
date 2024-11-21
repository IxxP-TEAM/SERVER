package com.ip.api.dto.production;

import com.ip.api.domain.ProductionAnalysis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductionAnalysisResponseDto {
	
	private Long analysisId;
	private Long productionId;
	private String issueCause;
	private String improvements;
	private String userName;
	
	
	public static ProductionAnalysisResponseDto from(ProductionAnalysis analysis) {
        return ProductionAnalysisResponseDto.builder()
                .analysisId(analysis.getAnalysisId())
                .productionId(analysis.getProduction().getProductionId())
                .issueCause(analysis.getIssueCause())
                .improvements(analysis.getImprovements())
                .userName(analysis.getUser().getUserName())
                .build();
    }
}
