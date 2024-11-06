package com.ip.api.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ip.api.apiPayload.code.ApiResponse;
import com.ip.api.dto.product.ProductRequestDto;
import com.ip.api.dto.product.ProductResponseDto;
import com.ip.api.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/products")
@RequiredArgsConstructor
@CrossOrigin
public class ProductController {

	private final ProductService productService;

	// 제품 등록
	@PostMapping("/create")
	public ApiResponse<ProductResponseDto> createProduct(@RequestBody ProductRequestDto productRequestDto) {
		ProductResponseDto productResponse = productService.createProduct(productRequestDto);
		return ApiResponse.of(productResponse);
	}

	// 제품 수정
	@PostMapping("/update/{id}")
	public ApiResponse<ProductResponseDto> updateProduct(@PathVariable("id") Long id,
			@RequestBody ProductRequestDto productRequestDto) {

		ProductResponseDto updatedProduct = productService.updateProduct(id, productRequestDto);
		return ApiResponse.of(updatedProduct);
	}

	// 제품 삭제
	@DeleteMapping("/delete/{id}")
	public ApiResponse<List<ProductResponseDto>> deleteProduct(@PathVariable("id") Long id, Pageable pageable) {

		productService.deleteProduct(id, pageable);
		List<ProductResponseDto> products = productService.getAllProducts(pageable);
		return ApiResponse.of(products);

	}

	// 제품 검색
	@GetMapping("/search")
	public ApiResponse<List<ProductResponseDto>> searchProducts(@RequestParam("name") String name, Pageable pageable) {
		List<ProductResponseDto> products = productService.searchProductsByName(name, pageable);
		return ApiResponse.of(products);
	}

	// 제품 리스트 조회
	@GetMapping
	public ApiResponse<List<ProductResponseDto>> getAllProducts(Pageable pageable) {
		List<ProductResponseDto> products = productService.getAllProducts(pageable);
		return ApiResponse.of(products);
	}

}
