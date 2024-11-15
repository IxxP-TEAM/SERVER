package com.ip.api.dto.inventory;

import java.util.List;

import org.springframework.data.domain.Page;

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
public class InventoryPageDto<T> {  // <T>를 추가하여 제네릭 클래스 선언

    private List<T> elements;          // 페이징된 데이터 리스트
    private int totalPages;            // 전체 페이지 수
    private long totalElements;        // 전체 요소 수
    private int currentPage;           // 현재 페이지 번호
    private int pageSize;              // 페이지당 요소 수
    private boolean isFirst;           // 첫 페이지 여부
    private boolean isLast;            // 마지막 페이지 여부
    private boolean hasNext;           // 다음 페이지 여부
    private boolean hasPrevious;       // 이전 페이지 여부
    private String sortBy;             // 정렬 기준 필드
    private String sortDirection;      // 정렬 방향 (asc/desc)

    // 제네릭 타입을 사용하는 from 메서드
    public static <T> InventoryPageDto<T> from(Page<T> page, String sortBy, String sortDirection) {
        return InventoryPageDto.<T>builder()
                .elements(page.getContent())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();
    }
}

