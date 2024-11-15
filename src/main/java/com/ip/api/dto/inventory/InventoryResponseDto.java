package com.ip.api.dto.inventory;

import java.time.LocalDate;

import com.ip.api.domain.Inventory;
import com.ip.api.domain.enums.ProductType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponseDto {

    private Long productId;                
    private String productName;    
    private ProductType productType;    
    private int currentQuantity;          
    private LocalDate expirationDate; 
    
    public static InventoryResponseDto fromEntity(Inventory inventory) {
        return InventoryResponseDto.builder()
                .productId(inventory.getProduct().getProductId())   
                .productName(inventory.getProduct().getProductName())
                .productType(inventory.getProduct().getProductType())
                .currentQuantity(inventory.getCurrentQuantity())
                .expirationDate(inventory.getExpirationDate())
                .build();
    }
}
