package com.ip.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.ip.api.domain.InventoryHistory;
import com.ip.api.domain.Product;
import com.ip.api.domain.enums.ChangeType;

public interface InventoryHistoryRepository extends JpaRepository<InventoryHistory, Long> {

	Page<InventoryHistory> findByChangeType(ChangeType changeType, Pageable pageable);

    Page<InventoryHistory> findAll(Pageable pageable);
    
    Page<InventoryHistory> findByProduct_ProductNameContainingAndChangeType(String productName, ChangeType changeType, Pageable pageable);
    Page<InventoryHistory> findByProduct_ProductNameContaining(String productName, Pageable pageable);

  


}
