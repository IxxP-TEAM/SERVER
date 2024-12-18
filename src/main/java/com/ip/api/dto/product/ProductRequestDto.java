package com.ip.api.dto.product;

import com.ip.api.domain.Product;
import com.ip.api.domain.enums.ProductType;

import jakarta.validation.constraints.Min;
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
	@Min(value = 0, message = "임계재고는 0 이상이어야 합니다.")
	private int safetyStockQuantity;
	private int productPrice;
	
	
	public Product toEntity() {
		return Product.builder()
				.productName(this.productName)
				.productType(this.productType)
				.safetyStockQuantity(this.safetyStockQuantity)
				.productPrice(this.productPrice)
				.build();
	}
}
