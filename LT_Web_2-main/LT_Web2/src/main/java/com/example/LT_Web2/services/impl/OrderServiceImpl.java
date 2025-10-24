package com.example.LT_Web2.services.impl;

import com.example.LT_Web2.dto.request.OrderRequest;
import com.example.LT_Web2.dto.response.OrderResponse;
import com.example.LT_Web2.dto.response.ReportResponse;
import com.example.LT_Web2.entity.*;
import com.example.LT_Web2.exception.BusinessException;
import com.example.LT_Web2.exception.ResourceNotFoundException;
import com.example.LT_Web2.repository.OrderItemRepository;
import com.example.LT_Web2.repository.OrderRepository;
import com.example.LT_Web2.services.OrderService;
import com.example.LT_Web2.services.ProductService;
import com.example.LT_Web2.services.TableService;
import com.example.LT_Web2.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final TableService tableService;
    private final ProductService productService;
    private final UserService userService;

    public Order createOrder(OrderRequest request, Long customerId) {
        // 1. Load table
        Tables table = tableService.findById(request.getTableId());

        // 2. Kiểm tra trạng thái bàn: phải là OCCUPIED hoặc RESERVED
        if (table.getStatus() != TableStatus.OCCUPIED && table.getStatus() != TableStatus.RESERVED) {
            throw new BusinessException("Chỉ có thể tạo đơn hàng cho bàn đang sử dụng");
        }

        // 3. Tạo đơn hàng
        Order order = new Order();
        order.setTable(table);
        order.setCustomer(userService.getUserById(customerId)); // ← cần inject userService
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        // 4. Tính tổng & lưu item
        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();
        for (OrderRequest.OrderItemRequest itemReq : request.getItems()) {
            Product product = productService.findById(itemReq.getProductId());
            BigDecimal price = product.getPrice();
            total = total.add(price.multiply(BigDecimal.valueOf(itemReq.getQuantity())));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemReq.getQuantity());
            item.setPriceAtOrder(price);
            items.add(item);
        }
        order.setTotalAmount(total);
        Order savedOrder = orderRepository.save(order);

        // Lưu item
        for (OrderItem item : items) {
            item.setOrder(savedOrder);
            orderItemRepository.save(item);
        }

        return savedOrder;
    }

    @Override
    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }


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