package com.ip.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ip.api.domain.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

}
