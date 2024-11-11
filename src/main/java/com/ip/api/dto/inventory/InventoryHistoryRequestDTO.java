package com.ip.api.dto.inventory;

import java.time.LocalDate;

import com.ip.api.domain.Inventory;
import com.ip.api.domain.InventoryHistory;
import com.ip.api.domain.Product;
import com.ip.api.domain.User;
import com.ip.api.domain.enums.ChangeType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryHistoryRequestDTO {
	
	private Long productId;
	private ChangeType changeType;
	private int quantity;
	private LocalDate expirationDate;
	private String userName;
	private String adjustment;
	private Long userId;
	
	public InventoryHistory toEntity(Product product, User user) {
	    return InventoryHistory.builder()
	            .product(product)               // Product 엔티티 객체 전달
	            .user(user)                     // User 엔티티 객체 전달
	            .changeType(this.changeType)     // 변동 유형
	            .changeQuantity(this.quantity)   // 변동 수량
	            .expirationDate(this.expirationDate) // 유통기한
	            .changeDate(LocalDate.now())     // 변동 날짜 (현재 날짜로 설정)
	            .adjustment(this.adjustment)     // 조정 사유 (있을 경우)
	            .build();
	}
	public Inventory toInventoryEntity(Product product) {
        return Inventory.builder()
                .product(product)
                .currentQuantity(this.quantity) // 입고된 수량을 현재 수량으로 설정
                .expirationDate(this.expirationDate)
                .build();
    }

}
