package com.ip.api.dto.inventory;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponseDto {
	
	private Long inventoryId;
	private String productName;    
    private String productType;    
    private int quantity;          
    private LocalDate expirationDate; 
	
}
