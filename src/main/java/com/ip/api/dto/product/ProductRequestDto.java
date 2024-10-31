package com.ip.api.dto.product;

import com.ip.api.domain.Product;
import com.ip.api.domain.enums.ProductType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {
	
	private String productName;
	private ProductType productType;
	private int safetyStockQuantity;
	
	
	public Product toEntity() {
		return Product.builder()
				.productName(this.productName)
				.productType(this.productType)
				.safetyStockQuantity(this.safetyStockQuantity)
				.build();
	}
}
