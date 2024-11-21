package com.ip.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ip.api.apiPayload.code.ApiResponse;
import com.ip.api.apiPayload.code.ErrorCode;
import com.ip.api.apiPayload.exception.BadRequestException;
import com.ip.api.apiPayload.exception.NotFoundException;
import com.ip.api.auth.AuthUser;
import com.ip.api.domain.User;
import com.ip.api.dto.production.ProductionAnalysisRequestDto;
import com.ip.api.dto.production.ProductionAnalysisResponseDto;
import com.ip.api.dto.production.ProductionListResponseDto;
import com.ip.api.dto.production.ProductionMaterialResponseDto;
import com.ip.api.dto.production.ProductionPageDto;
import com.ip.api.dto.production.ProductionRequestDto;
import com.ip.api.dto.production.ProductionResponseDto;
import com.ip.api.dto.production.ProductionResultRequestDto;
import com.ip.api.repository.UserRepository;
import com.ip.api.service.ProductionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/production")
@RequiredArgsConstructor
public class ProductionController {
	
	private final ProductionService productionService;
    private final UserRepository userRepository;
	
	
	// 생산 계획 등록
    @PostMapping("/create")
	public ApiResponse<ProductionResponseDto> createProduction(@RequestBody ProductionRequestDto productionRequestDto,
			@AuthUser User user){
		
		user = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.PRODUCTUSER_NOT_FOUND));
		
		ProductionResponseDto productionResponseDto = productionService.createProduction(productionRequestDto, user);
		
		return ApiResponse.of(productionResponseDto);
	}
    
    // 생산 계획 완료
    @PatchMapping("/update/{id}")
    public ApiResponse<ProductionResponseDto> updateProductionResult(
    		@PathVariable("id") Long productionId,
        @RequestBody ProductionResultRequestDto productionResultRequestDto) {
        
        ProductionResponseDto updatedProduction = productionService.updateProductionResult(productionId, productionResultRequestDto);
        return ApiResponse.of(updatedProduction);
    }
    
    // 생산 리스트 
    @GetMapping
    public ApiResponse<ProductionPageDto<ProductionListResponseDto>> getProductionList(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "productionId") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "asc") String sortDirection) {
    	
    	ProductionPageDto<ProductionListResponseDto> productionPageDto =
                productionService.getPagedProductionList(page, size, sortBy, sortDirection);

        // 서비스 호출
        return ApiResponse.of(productionPageDto);
    }
    
    // 생산 상세보기
    @GetMapping("/{productionId}")
    public ApiResponse<List<ProductionMaterialResponseDto>> getProductionDetails(
            @PathVariable("productionId") Long productionId) {
        
        List<ProductionMaterialResponseDto> details = productionService.getProductionDetailsByProductionId(productionId);
        return ApiResponse.of(details);
    }
    
    // 생산 분석 등록
    @PostMapping("/analysis/{id}")
    public ApiResponse<ProductionAnalysisResponseDto> registerProductionAnalysis(
            @PathVariable("id") Long productionId,
            @RequestBody ProductionAnalysisRequestDto requestDto,
            @AuthUser User user) {

        user = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.PRODUCTUSER_NOT_FOUND));

        ProductionAnalysisResponseDto responseDto = productionService.createProductionAnalysis(productionId, requestDto, user);
        return ApiResponse.of(responseDto);
    }
    
    // 생산 분석 조회
    @GetMapping("/analysis/{productionId}")
    public ApiResponse<ProductionAnalysisResponseDto> getProductionAnalysis(
            @PathVariable("productionId") Long productionId) {

        ProductionAnalysisResponseDto responseDto = productionService.getProductionAnalysisByProductionId(productionId);
        return ApiResponse.of(responseDto);
    }
    

}
