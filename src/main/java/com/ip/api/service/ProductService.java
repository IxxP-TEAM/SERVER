package com.ip.api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ip.api.apiPayload.code.ErrorCode;
import com.ip.api.apiPayload.exception.BadRequestException;
import com.ip.api.domain.Product;
import com.ip.api.dto.product.PageDto;
import com.ip.api.dto.product.ProductRequestDto;
import com.ip.api.dto.product.ProductResponseDto;
import com.ip.api.repository.InventoryHistoryRepository;
import com.ip.api.repository.InventoryRepository;
import com.ip.api.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
	
	private final ProductRepository productRepository;
	private final InventoryRepository inventoryRepository;
	private final InventoryHistoryRepository inventoryHistoryRepository;
	
	// 제품 등록
	public ProductResponseDto createProduct(ProductRequestDto productRequestDto) {
		
		if(productRepository.existsByProductName(productRequestDto.getProductName())) { // 제품 이름 중복 체크
			throw new BadRequestException(ErrorCode.PRODUCT_NAME_ALREADY_EXISTS);
		}
		
		Product product = productRequestDto.toEntity();
		Product saveProduct = productRepository.save(product);
		return ProductResponseDto.fromEntity(saveProduct);
		}
	
	// 제품 수정
	public ProductResponseDto updateProduct(Long id, ProductRequestDto productRequestDto) {
		
		Product product = productRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ErrorCode.PRODUCT_NOT_FOUND));
		
		// 재고에 제품이 존재하는지 확인
        if (inventoryRepository.existsByProductProductId(id)) {
            throw new BadRequestException(ErrorCode.PRODUCT_EXISTS_IN_INVENTORY); 
        }
		
		// 동일한 이름을 가진 다른 제품이 존재하는지 확인 (현재 수정 중인 제품 제외)
        if (productRepository.existsByProductNameAndProductIdNot(productRequestDto.getProductName(), id)) {
            throw new BadRequestException(ErrorCode.PRODUCT_NAME_ALREADY_EXISTS);
        }
        
		// 필드 업데이트
	    product.updateProduct(
	        productRequestDto.getProductName(),
	        productRequestDto.getProductType(),
	        productRequestDto.getSafetyStockQuantity(),
	        productRequestDto.getProductPrice()
	    );
	    
	    Product updatedProduct = productRepository.save(product);
		
		return ProductResponseDto.fromEntity(updatedProduct);
	}
	
	// 제품 삭제
	public PageDto deleteProduct(Long id, Pageable pageable) {
		
		
		// 재고에 제품이 존재하는지 확인
        if (inventoryRepository.existsByProductProductId(id)) {
            throw new BadRequestException(ErrorCode.PRODUCT_EXISTS_IN_INVENTORY); 
        }
	    productRepository.deleteById(id);
	    Page<Product> productPage = productRepository.findAll(pageable);
	    return PageDto.from(productPage); 
	}
	
	// 제품 검색
	public PageDto searchProductsByName(String productName, Pageable pageable) {
		
	    Page<Product> productPage = productRepository.findByProductNameContaining(productName, pageable);
	    return PageDto.from(productPage); 
	}

	
	// 제품 목록 조회
    public PageDto getAllProducts(Pageable pageable) {
    	
        Page<Product> productPage = productRepository.findAll(pageable);

        return PageDto.from(productPage);
    }
}
