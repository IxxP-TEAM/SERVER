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
public class ProductResponseDto {
	
	 private long productId;
	 private String productName;
	 private ProductType productType;
	 private int safetyStockQuantity;
	 private int productPrice;
	 
	 public static ProductResponseDto fromEntity(Product product) {
		 
		 return ProductResponseDto.builder()
				 .productId(product.getProductId())
				 .productName(product.getProductName())
				 .productType(product.getProductType())
				 .safetyStockQuantity(product.getSafetyStockQuantity())
				 .productPrice(product.getProductPrice())
				 .build();
	 }
}
