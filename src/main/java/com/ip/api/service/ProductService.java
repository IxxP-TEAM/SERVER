package com.ip.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ip.api.apiPayload.code.ErrorCode;
import com.ip.api.apiPayload.exception.BadRequestException;
import com.ip.api.apiPayload.exception.NotFoundException;
import com.ip.api.domain.Product;
import com.ip.api.dto.product.ProductRequestDto;
import com.ip.api.dto.product.ProductResponseDto;
import com.ip.api.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
	
	private final ProductRepository productRepository;
	
	// 제품 등록
	public ProductResponseDto createProduct(ProductRequestDto productRequestDto) {
		
		if(productRepository.existsByProductName(productRequestDto.getProductName())) {
			throw new BadRequestException(ErrorCode.PRODUCT_NAME_ALREADY_EXISTS);
		}
		
		Product product = productRequestDto.toEntity();
		Product saveProduct = productRepository.save(product);
		return ProductResponseDto.fromEntity(saveProduct);
		}
	
	// 제품 수정
	public ProductResponseDto updateProduct(Long id, ProductRequestDto productRequestDto) {
		Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException());
		
		if(productRepository.existsByProductName(productRequestDto.getProductName())) {
			throw new BadRequestException(ErrorCode.PRODUCT_NAME_ALREADY_EXISTS);
		}
		
		// 필드 업데이트
	    product.updateProduct(
	        productRequestDto.getProductName(),
	        productRequestDto.getProductType(),
	        productRequestDto.getSafetyStockQuantity()
	    );
	    
	    Product updatedProduct = productRepository.save(product);
		
		return ProductResponseDto.fromEntity(updatedProduct);
	}
	
	// 제품 삭제
	public List<ProductResponseDto> deleteProduct(Long id, Pageable pageable) {
		
		productRepository.deleteById(id);
		
		return productRepository.findAll(pageable)
                 				 .stream()
                 				 .map(ProductResponseDto::fromEntity)
                 				 .collect(Collectors.toList());
	}
	
	// 제품 검색
	public List<ProductResponseDto> searchProductsByName(String productName, Pageable pageable) {
	    return productRepository.findByProductNameContaining(productName, pageable)
	    		.stream()
				 .map(ProductResponseDto::fromEntity)
				 .collect(Collectors.toList());
	}
	
	// 제품 리스트
	public List<ProductResponseDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                                .stream()
                                .map(ProductResponseDto::fromEntity)
                                .collect(Collectors.toList());
    }
}
