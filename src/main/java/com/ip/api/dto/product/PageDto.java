package com.ip.api.dto.product;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import com.ip.api.domain.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageDto {

    private List<ProductResponseDto> elements;
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private int pageSize;

    public static PageDto from(Page<Product> productPage) {
        return PageDto
            .builder()
            .elements(productPage.getContent().stream().map(ProductResponseDto::fromEntity).collect(Collectors.toList()))
            .totalElements(productPage.getTotalElements())
            .totalPages(productPage.getTotalPages())
            .currentPage(productPage.getNumber())
            .pageSize(productPage.getSize())  
            .build();
    }
}
