package com.ip.api.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import com.ip.api.dto.product.PageDto;
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
	public ApiResponse<PageDto> deleteProduct(@PathVariable("id") Long id, Pageable pageable) {
		PageDto productsPage = productService.deleteProduct(id, pageable);
		return ApiResponse.of(productsPage);
	}

	// 제품 검색
	@GetMapping("/search")
	public ApiResponse<PageDto> searchProducts(@RequestParam("productName") String productName,
											   @RequestParam(name = "page", defaultValue = "0") int page,       // 기본 페이지 0
											   @RequestParam(name = "size", defaultValue = "10") int size,      // 기본 사이즈 10
											   @RequestParam(name = "sortBy", defaultValue = "productId") String sortBy,
											   @RequestParam(name = "direction", defaultValue = "asc") String direction) {
		
	    Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
	    Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
	    
	    PageDto productsPage = productService.searchProductsByName(productName, pageable);
	    return ApiResponse.of(productsPage);
	}

	// 제품 리스트 조회
	@GetMapping
	public ApiResponse<PageDto> getAllProducts(@RequestParam(name = "page", defaultValue = "0") int page,             // 기본 페이지 0
            								   @RequestParam(name = "size", defaultValue = "10") int size,            // 기본 사이즈 10
											   @RequestParam(name = "sortBy", defaultValue = "productId") String sortBy,
											   @RequestParam(name = "direction", defaultValue = "asc") String direction) {
		
		Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
		// 페이지와 정렬 정보로 Pageable 객체 생성
	    Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

	    // Service 호출
	    PageDto productsPage = productService.getAllProducts(pageable);
		return ApiResponse.of(productsPage);
	}

}
