package com.ip.api.dto.production;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductionMaterialRequestDto {
	
	private String productMaterialName; // 원재료 이름
    private int materialQuantity;
}
