package com.ip.api.repository;

import com.ip.api.domain.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {

    @Query("SELECT o FROM Orders o LEFT JOIN FETCH o.orderProducts WHERE o.orderId = :orderId")
    Optional<Orders> findByIdWithProducts(@Param("orderId") Long orderId);
}