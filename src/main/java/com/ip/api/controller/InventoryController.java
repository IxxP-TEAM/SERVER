package com.ip.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ip.api.apiPayload.code.ApiResponse;
import com.ip.api.apiPayload.code.ErrorCode;
import com.ip.api.apiPayload.exception.BadRequestException;
import com.ip.api.auth.AuthUser;
import com.ip.api.domain.User;
import com.ip.api.domain.enums.ChangeType;
import com.ip.api.dto.inventory.InventoryHistoryRequestDto;
import com.ip.api.dto.inventory.InventoryHistoryResponseDto;
import com.ip.api.dto.inventory.InventoryListDto;
import com.ip.api.dto.inventory.InventoryPageDto;
import com.ip.api.dto.inventory.InventoryResponseDto;
import com.ip.api.repository.ProductRepository;
import com.ip.api.repository.UserRepository;
import com.ip.api.service.InventoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
	
	private final InventoryService inventoryService;
//	private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // 입고 등록
    @PostMapping("/inbound")
    public ApiResponse<InventoryHistoryResponseDto> inboundInventory(@RequestBody InventoryHistoryRequestDto inventoryHistoryRequestDto,
    		@AuthUser User user) {
        
        user = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new BadRequestException(ErrorCode.PRODUCTUSER_NOT_FOUND));

        // Inventory 입고 처리
        InventoryHistoryResponseDto inventoryHistoryResponse = inventoryService.inboundInventory(inventoryHistoryRequestDto, user);
        
        return ApiResponse.of(inventoryHistoryResponse);
    }
    
    // 출고 등록
    @PostMapping("/outbound")
    public ApiResponse<InventoryHistoryResponseDto> outboundInventory(@RequestBody InventoryHistoryRequestDto inventoryHistoryRequestDto,
    																  @AuthUser User user) {
        
        user = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new BadRequestException(ErrorCode.PRODUCTUSER_NOT_FOUND));

        // Inventory 입고 처리
        InventoryHistoryResponseDto inventoryHistoryResponse = inventoryService.outboundInventory(inventoryHistoryRequestDto, user);
        
        return ApiResponse.of(inventoryHistoryResponse);
    }
    
    // 소모 등록
    @PostMapping("/consumption")
    public ApiResponse<InventoryHistoryResponseDto> consumptionInventory(@RequestBody InventoryHistoryRequestDto inventoryHistoryRequestDto,
    																		@AuthUser User user){
    	user = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new BadRequestException(ErrorCode.PRODUCTUSER_NOT_FOUND));

        // Inventory 입고 처리
        InventoryHistoryResponseDto inventoryHistoryResponse = inventoryService.consumptionInventory(inventoryHistoryRequestDto, user);
        
        return ApiResponse.of(inventoryHistoryResponse);
    }
    
    // 조정 등록
    @PostMapping("/adjustment")
    public ApiResponse<InventoryHistoryResponseDto> adjustmentInventory(@RequestBody InventoryHistoryRequestDto inventoryHistoryRequestDto,
    																		@AuthUser User user){
    	user = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new BadRequestException(ErrorCode.PRODUCTUSER_NOT_FOUND));

        // Inventory 입고 처리
        InventoryHistoryResponseDto inventoryHistoryResponse = inventoryService.adjustmentInventory(inventoryHistoryRequestDto, user);
        
        return ApiResponse.of(inventoryHistoryResponse);
    }
    
    // 재고 리스트 조회
    @GetMapping
    public ApiResponse<InventoryPageDto<InventoryListDto>> getInventoryList(
    		@RequestParam(name = "page", defaultValue = "0") int page,             // 기본 페이지 0
			   @RequestParam(name = "size", defaultValue = "10") int size,            // 기본 사이즈 10
			   @RequestParam(name = "sortBy", defaultValue = "productName") String sortBy,
			   @RequestParam(name = "direction", defaultValue = "asc") String direction,
			   @RequestParam(name = "searchTerm", required = false) String searchTerm) {

        InventoryPageDto<InventoryListDto> inventoryPage = inventoryService.getAllInventoryList(page, size, sortBy, direction, searchTerm);
        
        // ApiResponse로 감싸서 반환
        return ApiResponse.of(inventoryPage);
    }
    
    // 재고 상세 보기
    @GetMapping("/{productId}")
    public ApiResponse<List<InventoryResponseDto>> getInventoryDetailsByProductId(@PathVariable("productId") Long productId) {
        List<InventoryResponseDto> inventoryDetails = inventoryService.getInventoriesByProductId(productId);
        return ApiResponse.of(inventoryDetails);
    }
    
    // 재고 이력 목록 조회
    @GetMapping("/history")
    public ApiResponse<InventoryPageDto<InventoryHistoryResponseDto>> getInventoryHistoryList(
            @RequestParam(name = "page", defaultValue = "0") int page,               
            @RequestParam(name = "size", defaultValue = "10") int size,              
            @RequestParam(name = "sortBy", defaultValue = "changeDate") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction,
            @RequestParam(name = "searchQuery", required = false) String searchQuery) { // 통합 검색어 추가

        InventoryPageDto<InventoryHistoryResponseDto> inventoryHistoryPage = 
                inventoryService.getAllInventoryHistoryList(page, size, sortBy, direction, searchQuery);
        
        return ApiResponse.of(inventoryHistoryPage);
    }

    

}
