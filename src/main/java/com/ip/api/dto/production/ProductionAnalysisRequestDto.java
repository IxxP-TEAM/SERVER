package com.ip.api.dto.production;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductionAnalysisRequestDto {
	
	private Long productionId;
	private String issueCause;
	private String improvements;
	private Long userId;

}
