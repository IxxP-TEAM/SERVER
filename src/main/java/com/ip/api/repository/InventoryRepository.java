package com.ip.api.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ip.api.domain.Inventory;
import com.ip.api.domain.Product;
import com.ip.api.dto.inventory.InventoryListDto;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

	Optional<Inventory> findByProductAndExpirationDate(Product product, LocalDate expirationDate);

	List<Inventory> findByProductOrderByExpirationDateAsc(Product product);
	
	

	// 재고 목록 리스트
	@Query("SELECT new com.ip.api.dto.inventory.InventoryListDto(p.productId, p.productName, p.productType, total.totalQuantity, p.safetyStockQuantity) " +
		       "FROM Product p JOIN (SELECT i.product.productId AS productId, SUM(i.currentQuantity) AS totalQuantity " +
		       "FROM Inventory i " +
		       "GROUP BY i.product.productId) AS total ON p.productId = total.productId " +
		       "ORDER BY p.productName ASC")
		List<InventoryListDto> findAllGroupedByProduct();

	// 재고 목록 상세보기
	@Query("SELECT i FROM Inventory i WHERE i.product.productId = :productId ORDER BY i.expirationDate ASC")
    List<Inventory> getInventoryByProductId(@Param("productId") Long productId);
	
	// 재고 상세보기
	@Query("SELECT new com.ip.api.dto.inventory.InventoryListDto(p.productId, p.productName, p.productType, total.totalQuantity, p.safetyStockQuantity) " +
		       "FROM Product p JOIN (SELECT i.product.productId AS productId, SUM(i.currentQuantity) AS totalQuantity " +
		       "FROM Inventory i " +
		       "GROUP BY i.product.productId) AS total ON p.productId = total.productId " +
		       "WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
		List<InventoryListDto> findAllGroupedByProductWithSearch(@Param("searchTerm") String searchTerm);


	boolean existsByProductProductId(Long productId);

}
