package com.ip.api.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ip.api.apiPayload.code.ErrorCode;
import com.ip.api.apiPayload.exception.BadRequestException;
import com.ip.api.domain.Inventory;
import com.ip.api.domain.InventoryHistory;
import com.ip.api.domain.Product;
import com.ip.api.domain.User;
import com.ip.api.domain.enums.ChangeType;
import com.ip.api.domain.enums.ProductType;
import com.ip.api.dto.inventory.InventoryHistoryRequestDto;
import com.ip.api.dto.inventory.InventoryHistoryResponseDto;
import com.ip.api.dto.inventory.InventoryListDto;
import com.ip.api.dto.inventory.InventoryPageDto;
import com.ip.api.dto.inventory.InventoryResponseDto;
import com.ip.api.repository.InventoryHistoryRepository;
import com.ip.api.repository.InventoryRepository;
import com.ip.api.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {
	
	private final InventoryRepository inventoryRepository;
	
	
	private final ProductRepository productRepository;
	private final InventoryHistoryRepository inventoryHistoryRepository;
	
	// 재고 관리

	// 입고 
	public InventoryHistoryResponseDto inboundInventory(InventoryHistoryRequestDto inventoryHistoryRequestDto, User user) {
	    
	    // 입고 수량이 양수인지 확인
	    if (inventoryHistoryRequestDto.getQuantity() < 1) {
	        throw new BadRequestException(ErrorCode.INVENTORY_INVALID_QUANTITY);
	    }
	    
	    Product product = productRepository.findByProductName(inventoryHistoryRequestDto.getProductName())
	            .orElseThrow(() -> new BadRequestException(ErrorCode.PRODUCT_NOT_FOUND));

	    // Inventory에서 동일한 제품 ID와 유통기한을 가진 항목을 찾음
	    Optional<Inventory> existingInventory = inventoryRepository.findByProductAndExpirationDate(
	            product, inventoryHistoryRequestDto.getExpirationDate());

	    Inventory inventory;

	    if (existingInventory.isPresent()) {
	        // 동일한 유통기한의 재고가 있을 경우, 수량을 업데이트
	        inventory = existingInventory.get();
	        inventory.updateQuantity(inventoryHistoryRequestDto.getQuantity());
	    } else {
	        // 동일한 유통기한의 재고가 없을 경우, 새로운 재고 항목 생성
	        inventory = inventoryHistoryRequestDto.toInventoryEntity(product);
	    }

	    // 업데이트된 또는 새로 생성된 Inventory 저장
	    inventoryRepository.save(inventory);

	    // InventoryHistory 기록 생성
	    InventoryHistory inventoryHistory = inventoryHistoryRequestDto.toEntity(product, user);
	    inventoryHistory.setChangeType(ChangeType.입고);
	    InventoryHistory savedInventoryHistory = inventoryHistoryRepository.save(inventoryHistory);

	    return InventoryHistoryResponseDto.fromEntity(savedInventoryHistory);
	}
	
	// 출고 
	public InventoryHistoryResponseDto outboundInventory(InventoryHistoryRequestDto inventoryHistoryRequestDto, User user) {

	    // 출고 수량이 1 이상인지 확인
	    if (inventoryHistoryRequestDto.getQuantity() < 1) {
	        throw new BadRequestException(ErrorCode.INVENTORY_INVALID_QUANTITY);
	    }
	    
	    Product product = productRepository.findByProductName(inventoryHistoryRequestDto.getProductName())
	            .orElseThrow(() -> new BadRequestException(ErrorCode.PRODUCT_NOT_FOUND));

	    int remainingQuantity = inventoryHistoryRequestDto.getQuantity();
	    
	    // 전체 재고 수량 계산
	    List<Inventory> inventories = inventoryRepository.findByProductOrderByExpirationDateAsc(product);
	    int totalAvailableQuantity = inventories.stream()
	            .mapToInt(Inventory::getCurrentQuantity)
	            .sum();
	    
	    // 출고 수량이 재고보다 많은지 확인
	    if (inventoryHistoryRequestDto.getQuantity() > totalAvailableQuantity) {
	        throw new BadRequestException(ErrorCode.OUTBOUND_QUANTITY_EXCEEDS_STOCK);
	    }
	    

	    for (Inventory inventory : inventories) {
	        if (remainingQuantity < 1) break;

	        int currentQuantity = inventory.getCurrentQuantity();

	        if (currentQuantity >= remainingQuantity) {
	            inventory.updateQuantity(-remainingQuantity);
	            remainingQuantity = 0;
	        } else {
	            inventory.updateQuantity(-currentQuantity);
	            remainingQuantity -= currentQuantity;
	        }

	        // 수량이 0이면 삭제, 아니면 저장
	        if (inventory.getCurrentQuantity() == 0) {
	            inventoryRepository.delete(inventory);
	        } else {
	            inventoryRepository.save(inventory);
	        }
	    }

	    

	    // 출고 기록 생성 및 저장 (유통기한 제외)
	    InventoryHistory inventoryHistory = inventoryHistoryRequestDto.toEntity(product, user);
	    inventoryHistory.setChangeType(ChangeType.출고);
	    InventoryHistory savedInventoryHistory = inventoryHistoryRepository.save(inventoryHistory);

	    return InventoryHistoryResponseDto.fromEntity(savedInventoryHistory);
	}

	// 소모 (생산)
	public InventoryHistoryResponseDto consumptionInventory(InventoryHistoryRequestDto inventoryHistoryRequestDto, User user) {

	    // 소모 수량이 1 이상인지 확인
	    if (inventoryHistoryRequestDto.getQuantity() < 1) {
	        throw new BadRequestException(ErrorCode.INVENTORY_INVALID_QUANTITY);
	    }
	    
	    Product product = productRepository.findByProductName(inventoryHistoryRequestDto.getProductName())
	            .orElseThrow(() -> new BadRequestException(ErrorCode.PRODUCT_NOT_FOUND));

		if(ProductType.완제품.equals(product.getProductType())) {
			throw new BadRequestException(ErrorCode.PRODUCT_NOT_MATERIAL);
		}
	    
	    int remainingQuantity = inventoryHistoryRequestDto.getQuantity();

	    // 전체 재고 수량 계산
	    List<Inventory> inventories = inventoryRepository.findByProductOrderByExpirationDateAsc(product);
	    int totalAvailableQuantity = inventories.stream()
	            .mapToInt(Inventory::getCurrentQuantity)
	            .sum();
	    
	    // 소모 수량이 재고보다 많은지 확인
	    if (inventoryHistoryRequestDto.getQuantity() > totalAvailableQuantity) {
	        throw new BadRequestException(ErrorCode.OUTBOUND_QUANTITY_EXCEEDS_STOCK);
	    }

	    for (Inventory inventory : inventories) {
	        if (remainingQuantity < 1) break;

	        int currentQuantity = inventory.getCurrentQuantity();

	        if (currentQuantity >= remainingQuantity) {
	            inventory.updateQuantity(-remainingQuantity);
	            remainingQuantity = 0;
	        } else {
	            inventory.updateQuantity(-currentQuantity);
	            remainingQuantity -= currentQuantity;
	        }

	        // 수량이 0이면 삭제, 아니면 저장
	        if (inventory.getCurrentQuantity() == 0) {
	            inventoryRepository.delete(inventory);
	        } else {
	            inventoryRepository.save(inventory);
	        }
	    }

	    // 소모 기록 생성 및 저장 (유통기한 제외)
	    InventoryHistory inventoryHistory = inventoryHistoryRequestDto.toEntity(product, user);
	    inventoryHistory.setChangeType(ChangeType.소모);
	    InventoryHistory savedInventoryHistory = inventoryHistoryRepository.save(inventoryHistory);

	    return InventoryHistoryResponseDto.fromEntity(savedInventoryHistory);
	}
	
	// 조정 ( 수정? )
	public InventoryHistoryResponseDto adjustmentInventory(InventoryHistoryRequestDto inventoryHistoryRequestDto, User user) {
		
		// 조정 수량이 0인지 아닌지 확인
	    if (inventoryHistoryRequestDto.getQuantity() == 0) {
	        throw new BadRequestException(ErrorCode.INVENTORY_INVALID_QUANTITY);
	    }

	    // 조정 사유가 없으면 예외 발생
	    if (inventoryHistoryRequestDto.getAdjustment() == null || inventoryHistoryRequestDto.getAdjustment().isEmpty()) {
	        throw new BadRequestException(ErrorCode.INVENTORY_ADJUSTMENT_REASON_REQUIRED);
	    }
		
		// 제품을 조회
	    Product product = productRepository.findByProductName(inventoryHistoryRequestDto.getProductName())
	            .orElseThrow(() -> new BadRequestException(ErrorCode.PRODUCT_NOT_FOUND));

	    // 특정 유통기한을 가진 재고 조회
	    Inventory inventory = inventoryRepository.findByProductAndExpirationDate(
	            product, inventoryHistoryRequestDto.getExpirationDate())
	            .orElseThrow(() -> new BadRequestException(ErrorCode.INVENTORY_NOT_FOUND));
	    
	    // 기존 수량에 조정 수량을 더하거나 뺌
	    int newQuantity = inventory.getCurrentQuantity() + inventoryHistoryRequestDto.getQuantity();
	    
	    // 수량이 0 미만이면 예외 발생
	    if (newQuantity < 0) {
	        throw new BadRequestException(ErrorCode.INVENTORY_INVALID_QUANTITY);
	    }

	    // 수량 업데이트 (양수일 경우 더하고 음수일 경우 빼기)
	    inventory.updateQuantity(inventoryHistoryRequestDto.getQuantity());

	    // 수량이 0이면 삭제, 그렇지 않으면 저장
	    if (inventory.getCurrentQuantity() == 0) {
	        inventoryRepository.delete(inventory);
	    } else {
	        inventoryRepository.save(inventory);
	    }
		
	    // 조정 기록 생성 및 저장
	    InventoryHistory inventoryHistory = inventoryHistoryRequestDto.toEntity(product, user);
	    inventoryHistory.setChangeType(ChangeType.조정);
	    InventoryHistory savedInventoryHistory = inventoryHistoryRepository.save(inventoryHistory);

	    return InventoryHistoryResponseDto.fromEntity(savedInventoryHistory);
	}
	
	// 재고 목록 ( 제품명, 종류, 수량, 임계수량)
	public InventoryPageDto<InventoryListDto> getAllInventoryList(int page, int size, String sortBy, String direction, String searchTerm) {
	    // 유효하지 않은 정렬 기준 검증
	    if (!sortBy.equals("productName") && !sortBy.equals("totalQuantity")) {
	        throw new BadRequestException(ErrorCode.INVALID_INPUT_VALUE);
	    }

	    // 정렬 방향 설정
	    Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

	    // 전체 데이터를 불러온 후 수동 정렬 적용
	    List<InventoryListDto> inventoryList;
	    if (searchTerm != null && !searchTerm.isEmpty()) {
	        inventoryList = inventoryRepository.findAllGroupedByProductWithSearch(searchTerm);
	    } else {
	        inventoryList = inventoryRepository.findAllGroupedByProduct();
	    }

	    // 수동 정렬 수행
	    if (sortBy.equals("productName")) {
	        inventoryList.sort(sortDirection == Sort.Direction.ASC ? 
	                           Comparator.comparing(InventoryListDto::getProductName) : 
	                           Comparator.comparing(InventoryListDto::getProductName).reversed());
	    } else if (sortBy.equals("totalQuantity")) {
	        inventoryList.sort(sortDirection == Sort.Direction.ASC ? 
	                           Comparator.comparing(InventoryListDto::getTotalQuantity) : 
	                           Comparator.comparing(InventoryListDto::getTotalQuantity).reversed());
	    }

	    // 페이지에 맞게 데이터 분리
	    int start = Math.min(page * size, inventoryList.size());
	    int end = Math.min((page + 1) * size, inventoryList.size());
	    List<InventoryListDto> pageContent = inventoryList.subList(start, end);

	    // 수동 페이징을 위한 Page 객체 생성
	    Page<InventoryListDto> sortedPage = new PageImpl<>(pageContent, PageRequest.of(page, size), inventoryList.size());

	    return InventoryPageDto.from(sortedPage, sortBy, direction);
	}
	
	// 재고 목록 상세보기 ( 제품별(유통기한별 각 재고수)
	public List<InventoryResponseDto> getInventoriesByProductId(Long productId) {
        List<Inventory> inventories = inventoryRepository.getInventoryByProductId(productId);
        return inventories.stream()
                .map(InventoryResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
	
	// 재고 이력 목록 조회
	public InventoryPageDto<InventoryHistoryResponseDto> getAllInventoryHistoryList(int page, int size, String sortBy, String direction, String searchQuery) {
	    // 유효한 정렬 필드만 허용
	    if (!sortBy.equals("productName") && !sortBy.equals("changeDate") && !sortBy.equals("changeType")) {
	        throw new BadRequestException(ErrorCode.INVALID_INPUT_VALUE);
	    }

	    // 정렬 설정
	    Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
	    Sort sort = Sort.by(sortDirection, sortBy.equals("productName") ? "product.productName" : sortBy);
	    Pageable pageable = PageRequest.of(page, size, sort);

	    Page<InventoryHistory> historyPage;

	    // `searchQuery`를 기반으로 `productName` 또는 `changeType` 검색
	    ChangeType changeType = null;
	    try {
	        changeType = ChangeType.valueOf(searchQuery); // `searchQuery`가 `ChangeType`에 있는 값인지 확인
	    } catch (IllegalArgumentException e) {
	        // `searchQuery`가 `ChangeType`이 아니면 `productName`으로 검색
	    }

	    if (changeType != null ) {
	        historyPage = inventoryHistoryRepository.findByChangeType(changeType, pageable);
	    } else if (searchQuery != null && !searchQuery.isEmpty()) {
	        historyPage = inventoryHistoryRepository.findByProduct_ProductNameContaining(searchQuery, pageable);
	    } else {
	        historyPage = inventoryHistoryRepository.findAll(pageable);
	    }

	    Page<InventoryHistoryResponseDto> responseDtoPage = historyPage.map(InventoryHistoryResponseDto::fromEntity);
	    return InventoryPageDto.from(responseDtoPage, sortBy, direction);
	}




	
}

	

