package com.ip.api.dto.inventory;

import java.time.LocalDate;

import com.ip.api.domain.InventoryHistory;
import com.ip.api.domain.enums.ChangeType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryHistoryResponseDto {

		private Long historyId;
		private Long productId;  
		private ChangeType changeType;
	    private String productName;  
	    private int changeQuantity;  
	    private LocalDate expirationDate;  
	    private LocalDate changeDate; 
	    private String adjustment;  
//	    private Long userId;  
	    private String userName;
	    
	    public static InventoryHistoryResponseDto fromEntity(InventoryHistory inventoryHistory) {
	        return InventoryHistoryResponseDto.builder()
	            .historyId(inventoryHistory.getHistoryId())
	            .productId(inventoryHistory.getProduct().getProductId())
	            .changeType(inventoryHistory.getChangeType())
	            .productName(inventoryHistory.getProduct().getProductName())
	            .changeQuantity(inventoryHistory.getChangeQuantity())
	            .expirationDate(inventoryHistory.getExpirationDate())
	            .changeDate(inventoryHistory.getChangeDate())
	            .adjustment(inventoryHistory.getAdjustment())
//	            .userId(inventoryHistory.getUser().getUserId())
	            .userName(inventoryHistory.getUser().getUserName())
	            .build();
	    }
	    
}
