package com.ip.api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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
		
		Product product = productRequestDto.toEntity();
		Product saveProduct = productRepository.save(product);
		return ProductResponseDto.fromEntity(saveProduct);
		}
	
	// 제품 리스트
	public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll()
                                .stream()
                                .map(ProductResponseDto::fromEntity)
                                .collect(Collectors.toList());
    }
}
