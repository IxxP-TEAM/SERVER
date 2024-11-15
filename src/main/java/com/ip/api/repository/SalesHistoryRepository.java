package com.ip.api.repository;

import com.ip.api.domain.SalesHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalesHistoryRepository extends JpaRepository<SalesHistory, Long> {

    // 전체 매출 데이터 조회
    @Query("SELECT sh FROM SalesHistory sh ORDER BY sh.salesDate DESC")
    List<SalesHistory> findAllSales();

    // 특정 주문 ID로 SalesHistory 조회
    Optional<SalesHistory> findByOrderOrderId(Long orderId);

    // 특정 주문 ID로 SalesHistory 삭제
    void deleteByOrderOrderId(Long orderId);

    // 고객사별 매출 조회
    @Query("SELECT sh FROM SalesHistory sh WHERE sh.customer.customerId = :customerId ORDER BY sh.salesDate DESC")
    List<SalesHistory> findByCustomerId(Long customerId);

    // 사원별 매출 조회
    @Query("SELECT sh FROM SalesHistory sh WHERE sh.user.userId = :userId ORDER BY sh.salesDate DESC")
    List<SalesHistory> findByUserId(Long userId);

    // 특정 기간 내 매출 조회
    List<SalesHistory> findBySalesDateBetween(LocalDate startDate, LocalDate endDate);

    // 전체 매출 합계 조회
    @Query("SELECT SUM(sh.salesAmount) FROM SalesHistory sh")
    BigDecimal getTotalSales();

    // 고객사별 총 매출 합계
    @Query("SELECT sh.customer.customerId, SUM(sh.salesAmount) FROM SalesHistory sh GROUP BY sh.customer.customerId")
    List<Object[]> getTotalSalesByCustomer();

    // 사원별 총 매출 합계
    @Query("SELECT sh.user.userId, SUM(sh.salesAmount) FROM SalesHistory sh GROUP BY sh.user.userId")
    List<Object[]> getTotalSalesByUser();

    // 특정 기간 동안 고객사별 총 매출 합계
    @Query("SELECT sh.customer.customerId, SUM(sh.salesAmount) FROM SalesHistory sh " +
            "WHERE sh.salesDate BETWEEN :startDate AND :endDate GROUP BY sh.customer.customerId")
    List<Object[]> getTotalSalesByCustomerAndDate(LocalDate startDate, LocalDate endDate);

    // 특정 기간 동안 사원별 총 매출 합계
    @Query("SELECT sh.user.userId, SUM(sh.salesAmount) FROM SalesHistory sh " +
            "WHERE sh.salesDate BETWEEN :startDate AND :endDate GROUP BY sh.user.userId")
    List<Object[]> getTotalSalesByUserAndDate(LocalDate startDate, LocalDate endDate);

    //월별 매출 통계
    @Query("SELECT FUNCTION('MONTH', sh.salesDate) AS month, SUM(sh.salesAmount) AS totalSales " +
            "FROM SalesHistory sh " +
            "WHERE sh.salesDate BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('MONTH', sh.salesDate) " +
            "ORDER BY month")
    List<Object[]> getMonthlySalesStatistics(LocalDate startDate, LocalDate endDate);

    //고객사의 상위 매출 기여도
    @Query("SELECT sh.customer.customerId, SUM(sh.salesAmount) AS totalSales " +
            "FROM SalesHistory sh " +
            "GROUP BY sh.customer.customerId " +
            "ORDER BY totalSales DESC")
    List<Object[]> getTopCustomersBySales();

    //사원의 상위 매출 기여도
    @Query("SELECT sh.user.userId, SUM(sh.salesAmount) AS totalSales " +
            "FROM SalesHistory sh " +
            "GROUP BY sh.user.userId " +
            "ORDER BY totalSales DESC")
    List<Object[]> getTopSalespersonsBySales();

    //매출 데이터 페이징
    @Query("SELECT sh FROM SalesHistory sh ORDER BY sh.salesDate DESC")
    Page<SalesHistory> findAllSales(Pageable pageable);


}
