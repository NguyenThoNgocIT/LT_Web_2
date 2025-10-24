package com.example.LT_Web2.services.impl;

import com.example.LT_Web2.dto.response.OrderResponse;
import com.example.LT_Web2.dto.response.ReportResponse;
import com.example.LT_Web2.entity.Order;
import com.example.LT_Web2.entity.OrderItem;
import com.example.LT_Web2.entity.OrderStatus;
import com.example.LT_Web2.entity.TableStatus;
import com.example.LT_Web2.exception.ResourceNotFoundException;
import com.example.LT_Web2.repository.OrderItemRepository;
import com.example.LT_Web2.repository.OrderRepository;
import com.example.LT_Web2.services.OrderService;
import com.example.LT_Web2.services.TableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final TableService tableService;

    @Override
    public OrderResponse getOrderDetail(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại"));
        List<OrderItem> items = orderItemRepository.findByOrderId(id);
        return new OrderResponse(order, items);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(order -> {
                    List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
                    return new OrderResponse(order, items);
                })
                .toList();
    }

    @Override
    public List<OrderResponse> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(order -> {
                    List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
                    return new OrderResponse(order, items);
                })
                .toList();
    }

    @Override
    public Order updateOrderStatus(Long id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại"));

        // Cập nhật trạng thái bàn khi cần
        if (newStatus == OrderStatus.COMPLETED) {
            tableService.updateStatus(order.getTable().getId(), TableStatus.COMPLETED);
        } else if (newStatus == OrderStatus.PREPARING || newStatus == OrderStatus.SERVED) {
            // Không đổi trạng thái bàn
        }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    // ===== BÁO CÁO =====
    @Override
    public ReportResponse getDailyReport() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        BigDecimal revenue = orderRepository.sumRevenueByDateRange(start, end);
        long customers = orderRepository.countByCreatedAtBetweenAndStatus(start, end, OrderStatus.COMPLETED);
        return new ReportResponse(customers, revenue, "Today");
    }

    @Override
    public ReportResponse getWeeklyReport() {
        LocalDate today = LocalDate.now();
        LocalDate start = today.with(DayOfWeek.MONDAY);
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = start.plusWeeks(1).atStartOfDay();
        BigDecimal revenue = orderRepository.sumRevenueByDateRange(startTime, endTime);
        long customers = orderRepository.countByCreatedAtBetweenAndStatus(startTime, endTime, OrderStatus.COMPLETED);
        return new ReportResponse(customers, revenue, "This week");
    }

    @Override
    public ReportResponse getMonthlyReport() {
        LocalDate today = LocalDate.now();
        LocalDate start = today.withDayOfMonth(1);
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = start.plusMonths(1).atStartOfDay();
        BigDecimal revenue = orderRepository.sumRevenueByDateRange(startTime, endTime);
        long customers = orderRepository.countByCreatedAtBetweenAndStatus(startTime, endTime, OrderStatus.COMPLETED);
        return new ReportResponse(customers, revenue, "This month");
    }
}