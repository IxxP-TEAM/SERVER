package com.ip.api.service;

import org.springframework.stereotype.Service;

import com.ip.api.dto.inventory.InventoryHistoryRequestDTO;
import com.ip.api.dto.inventory.InventoryResponseDto;
import com.ip.api.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {
	
	private final InventoryRepository inventoryRepository;
	
	// 재고 관리

	// 입고 
	public InventoryResponseDto inboundInventory(InventoryHistoryRequestDTO inventoryHistoryRequestDTO) {
		return null;
		
	}
	
	// 출고 
	// 소모
	// 조정 ( 수정? )
	// 재고 목록 ( 제품명, 종류, 수량, 임계수량)
	// 재고 목록 상세보기 ( 제품별(유통기한별 각 재고수)
	// 재고 이력 목록
	
}
