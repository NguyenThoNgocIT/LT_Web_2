package com.example.LT_Web2.repository;

import com.example.LT_Web2.entity.Order;
import com.example.LT_Web2.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>{
    List<Order> findByCustomerId(Long customerId);
    List<Order> findByTableId(Long tableId);

    // Báo cáo: doanh thu theo ngày
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.createdAt >= :start AND o.createdAt < :end AND o.status = com.example.LT_Web2.entity.OrderStatus.COMPLETED")
    BigDecimal sumRevenueByDateRange(LocalDateTime start, LocalDateTime end);

    // Báo cáo: số lượng khách (mỗi đơn = 1 khách)
    long countByCreatedAtBetweenAndStatus(LocalDateTime start, LocalDateTime end, OrderStatus status);
}
