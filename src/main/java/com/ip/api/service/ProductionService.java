package com.ip.api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ip.api.apiPayload.code.ErrorCode;
import com.ip.api.apiPayload.exception.BadRequestException;
import com.ip.api.apiPayload.exception.NotFoundException;
import com.ip.api.domain.Product;
import com.ip.api.domain.Production;
import com.ip.api.domain.ProductionAnalysis;
import com.ip.api.domain.ProductionMaterial;
import com.ip.api.domain.User;
import com.ip.api.domain.enums.ProductType;
import com.ip.api.domain.enums.ProductionStatus;
import com.ip.api.dto.production.ProductionAnalysisRequestDto;
import com.ip.api.dto.production.ProductionAnalysisResponseDto;
import com.ip.api.dto.production.ProductionListResponseDto;
import com.ip.api.dto.production.ProductionMaterialResponseDto;
import com.ip.api.dto.production.ProductionPageDto;
import com.ip.api.dto.production.ProductionRequestDto;
import com.ip.api.dto.production.ProductionResponseDto;
import com.ip.api.dto.production.ProductionResultRequestDto;
import com.ip.api.repository.ProductRepository;
import com.ip.api.repository.ProductionAnalysisRepository;
import com.ip.api.repository.ProductionMaterialRepository;
import com.ip.api.repository.ProductionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductionService {
	
	private final ProductRepository productRepository;
	private final ProductionRepository productionRepository;
	private final ProductionMaterialRepository productionMaterialRepository;
	private final ProductionAnalysisRepository productionAnalysisRepository;
	
	// 생산 관리
	
	// 생산 계획(원재료 포함) 등록
	@Transactional
	public ProductionResponseDto createProduction(ProductionRequestDto productionRequestDto, User user) {
		
		// 생산할 제품 등록 확인
		Product product = productRepository.findByProductName(productionRequestDto.getProductName())
				.orElseThrow(() -> new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
		
		// 완제품 여부 검증
	    if (!product.getProductType().equals(ProductType.완제품)) {
	        throw new BadRequestException(ErrorCode.PRODUCT_NOT_FINISHED); 
	    }
	    
	    Production production = Production.builder()
	    		.product(product)
	            .user(user)
	            .startDate(productionRequestDto.getStartDate())
	            .endDate(productionRequestDto.getEndDate())
	            .targetQuantity(productionRequestDto.getTargetQuantity())
	            .productionStatus(productionRequestDto.getProductionStatus())
	            .build();

	    productionRepository.save(production); // 생산 계획 저장
	    
	    // 생산 원재료 리스트 처리 및 저장
	    List<ProductionMaterial> productionMaterials = productionRequestDto.getMaterials().stream()
	            .map(materialDto -> {
	                // 원재료 확인
	                Product materialProduct = productRepository.findByProductName(materialDto.getProductMaterialName())
	                        .orElseThrow(() -> new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
	                
	                if (!materialProduct.getProductType().equals(ProductType.원재료)) {
	        	        throw new BadRequestException(ErrorCode.PRODUCT_NOT_MATERIAL); 
	        	    }

	                return ProductionMaterial.builder()
	                        .production(production) // 연관된 생산 계획 설정
	                        .product(materialProduct) // 원재료 설정
	                        .consumedQuantity(materialDto.getMaterialQuantity()) // 소모 수량 설정
	                        .build();
	            })
	            .collect(Collectors.toList());

	    productionMaterialRepository.saveAll(productionMaterials);
	    
	    
	    return ProductionResponseDto.from(production, productionMaterials);		
		
	}
	
	// 생산 결과 등록 ( update ? )
	public ProductionResponseDto updateProductionResult(Long productionId, ProductionResultRequestDto productionResultRequestDto) {
        // 1. 생산 데이터 조회
        Production production = productionRepository.findById(productionId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.PRODUCTION_NOT_FOUND));

        // 2. 상태 확인
        if (ProductionStatus.완료.equals(production.getProductionStatus())) {
            throw new BadRequestException(ErrorCode.PRODUCTION_NOT_STATUS);
        }

        // 3. 결과 업데이트
        production.updateResult(productionResultRequestDto.getResultQuantity());
        productionRepository.save(production);
        
        List<ProductionMaterial> materials = productionMaterialRepository.findByProduction_ProductionId(productionId);
        // 4. 저장 및 응답 반환
        return ProductionResponseDto.from(production, materials);
    }
	

	
	// 생산 이력 전체 조회( 생산 계획 테이블 + 생산 원재료 테이블 불러오기 + 페이징 + 정렬( id 최신순, status 상태 )
	public ProductionPageDto<ProductionListResponseDto> getPagedProductionList(int page, int size, String sortBy, String sortDirection) {
		
		if (!sortBy.equals("productionId") && !sortBy.equals("productionStatus")) {
	        sortBy = "productionId"; // 기본 정렬 기준
	    }
	    // 정렬 객체 생성
	    Sort sort = Sort.by(
	        sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
	        sortBy
	    );

	    // Pageable 생성
	    Pageable pageable = PageRequest.of(page, size, sort);

	    // Production 페이징 처리
	    Page<Production> productionPage = productionRepository.findAll(pageable);

	    // DTO 변환 및 반환
	    Page<ProductionListResponseDto> dtoPage = productionPage.map(ProductionListResponseDto::from);
	    return ProductionPageDto.from(dtoPage, sortBy, sortDirection);
	}

	// 생산 목록 상세보기
	public List<ProductionMaterialResponseDto> getProductionDetailsByProductionId(Long productionId) {
	    // 생산 계획에 해당하는 원재료 조회
	    List<ProductionMaterial> productionMaterials = productionMaterialRepository.findByProduction_ProductionId(productionId);

	    // DTO 변환 후 반환
	    return productionMaterials.stream()
	            .map(ProductionMaterialResponseDto::from)
	            .collect(Collectors.toList());
	}
	
	// 생산 분석 등록 
	public ProductionAnalysisResponseDto createProductionAnalysis(Long productionId, ProductionAnalysisRequestDto requestDto, User user) {
	    // 1. 생산 계획 조회
	    Production production = productionRepository.findById(productionId)
	        .orElseThrow(() -> new NotFoundException(ErrorCode.PRODUCTION_NOT_FOUND));
	    
	    // 2. 생산 상태 확인 (완료 상태인지 체크)
	    if (!ProductionStatus.완료.equals(production.getProductionStatus())) {
	        throw new BadRequestException(ErrorCode.PRODUCTION_NOT_STATUS_FINISHED);
	    }

	    // 3. 생산 분석 중복 확인
	    boolean analysisExists = productionAnalysisRepository.existsByProduction(production);
	    if (analysisExists) {
	        throw new BadRequestException(ErrorCode.PRODUCTION_ALREADY_EXISTS);
	    }


	    ProductionAnalysis analysis = ProductionAnalysis.builder()
	        .production(production)
	        .issueCause(requestDto.getIssueCause())
	        .improvements(requestDto.getImprovements())
	        .user(user)
	        .build();

	    productionAnalysisRepository.save(analysis);

	    return ProductionAnalysisResponseDto.from(analysis);
	}
	
	// 생산 분석 조회
	public ProductionAnalysisResponseDto getProductionAnalysisByProductionId(Long productionId) {
	    ProductionAnalysis analysis = productionAnalysisRepository.findByProduction_ProductionId(productionId);

	    if (analysis == null) {
	        throw new NotFoundException(ErrorCode.PRODUCTION_NOT_ANALYSIS);
	    }

	    return ProductionAnalysisResponseDto.from(analysis);
	}
}
