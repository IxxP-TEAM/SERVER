package com.ip.api.dto.inventory;

import com.ip.api.domain.Product;
import com.ip.api.domain.enums.ProductType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
public class InventoryListDto {
	private Long productId; 
    private String productName;           
    private ProductType productType;           
    private Long totalQuantity;      // 총 수량 (유통기한 고려하지 않음)
    private int safetyStockQuantity; 
    
    public InventoryListDto(Long productId, String productName, ProductType productType, Long totalQuantity, int safetyStockQuantity) {
    	this.productId = productId;
        this.productName = productName;
        this.productType = productType;
        this.totalQuantity = totalQuantity;
        this.safetyStockQuantity = safetyStockQuantity;
    }
    

    public static InventoryListDto from(Product product, Long totalQuantity) {
        return InventoryListDto.builder()
        		.productId(product.getProductId())
                .productName(product.getProductName())
                .productType(product.getProductType())
                .totalQuantity(totalQuantity)
                .safetyStockQuantity(product.getSafetyStockQuantity())
                .build();
    }
}
