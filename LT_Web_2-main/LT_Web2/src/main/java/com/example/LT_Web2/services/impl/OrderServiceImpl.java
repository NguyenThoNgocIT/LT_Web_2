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
    @Override
    public java.util.Map<String, Object> getRevenueStatistics(String type, java.time.LocalDate from,
            java.time.LocalDate to) {
        // Chuẩn hóa khoảng thời gian
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        java.util.List<String> labels = new java.util.ArrayList<>();
        java.util.List<java.math.BigDecimal> values = new java.util.ArrayList<>();
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;

        java.util.List<Order> orders = orderRepository.findAll();
        if (from != null && to != null) {
            orders = orders.stream()
                    .filter(o -> !o.getCreatedAt().toLocalDate().isBefore(from)
                            && !o.getCreatedAt().toLocalDate().isAfter(to))
                    .toList();
        }

        if ("day".equalsIgnoreCase(type)) {
            // Thống kê từng ngày
            java.util.Map<java.time.LocalDate, java.math.BigDecimal> map = new java.util.TreeMap<>();
            for (Order o : orders) {
                java.time.LocalDate d = o.getCreatedAt().toLocalDate();
                map.put(d, map.getOrDefault(d, java.math.BigDecimal.ZERO).add(o.getTotalAmount()));
            }
            for (var entry : map.entrySet()) {
                labels.add(entry.getKey().toString());
                values.add(entry.getValue());
                total = total.add(entry.getValue());
            }
        } else if ("week".equalsIgnoreCase(type)) {
            // Thống kê theo tuần (ISO week)
            java.util.Map<String, java.math.BigDecimal> map = new java.util.TreeMap<>();
            for (Order o : orders) {
                java.time.LocalDate d = o.getCreatedAt().toLocalDate();
                java.time.temporal.WeekFields wf = java.time.temporal.WeekFields.ISO;
                int week = d.get(wf.weekOfWeekBasedYear());
                int year = d.getYear();
                String key = year + "-W" + week;
                map.put(key, map.getOrDefault(key, java.math.BigDecimal.ZERO).add(o.getTotalAmount()));
            }
            for (var entry : map.entrySet()) {
                labels.add(entry.getKey());
                values.add(entry.getValue());
                total = total.add(entry.getValue());
            }
        } else if ("month".equalsIgnoreCase(type)) {
            // Thống kê theo tháng
            java.util.Map<String, java.math.BigDecimal> map = new java.util.TreeMap<>();
            for (Order o : orders) {
                java.time.LocalDate d = o.getCreatedAt().toLocalDate();
                String key = d.getYear() + "-" + String.format("%02d", d.getMonthValue());
                map.put(key, map.getOrDefault(key, java.math.BigDecimal.ZERO).add(o.getTotalAmount()));
            }
            for (var entry : map.entrySet()) {
                labels.add(entry.getKey());
                values.add(entry.getValue());
                total = total.add(entry.getValue());
            }
        }
        result.put("labels", labels);
        result.put("values", values);
        result.put("total", total);
        return result;
    }

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final TableService tableService;
    private final ProductService productService;
    private final UserService userService;
    private final com.example.LT_Web2.repository.ReservationRepository reservationRepository;

    public Order createOrder(OrderRequest request, Long customerId) {
        Order order = new Order();

        // 1. Xử lý table (nếu có hoặc tự động từ reservation)
        if (request.getTableId() != null) {
            // Case 1: Đơn hàng tại bàn (khách ngồi tại quán) - đã chỉ định bàn
            Tables table = tableService.findById(request.getTableId());

            // Kiểm tra trạng thái bàn: phải là OCCUPIED hoặc RESERVED
            if (table.getStatus() != TableStatus.OCCUPIED && table.getStatus() != TableStatus.RESERVED) {
                throw new BusinessException("Chỉ có thể tạo đơn hàng cho bàn đang sử dụng hoặc đã đặt");
            }
            order.setTable(table);
            System.out.println("✅ [Order] Tạo đơn cho bàn #" + table.getId() + " - " + table.getName());
        } else {
            // Case 2: Kiểm tra xem user có reservation đang hoạt động không
            List<com.example.LT_Web2.entity.Reservation> activeReservations = reservationRepository
                    .findActiveReservationsByCustomer(customerId);

            if (!activeReservations.isEmpty()) {
                // User có reservation -> gán bàn từ reservation đầu tiên
                com.example.LT_Web2.entity.Reservation reservation = activeReservations.get(0);
                Tables reservedTable = reservation.getTable();
                order.setTable(reservedTable);

                // Cập nhật trạng thái bàn thành OCCUPIED nếu đang RESERVED
                if (reservedTable.getStatus() == TableStatus.RESERVED) {
                    reservedTable.setStatus(TableStatus.OCCUPIED);
                    tableService.save(reservedTable);
                }

                System.out.println("✅ [Order] User có reservation -> Tạo đơn cho bàn #" + reservedTable.getId() + " - "
                        + reservedTable.getName());
            } else {
                // Case 3: Đơn hàng mang đi/giao hàng (khách vãng lai, không có bàn)
                System.out.println("✅ [Order] Tạo đơn mang đi (không có bàn)");
            }
        }

        // 2. Tạo đơn hàng
        order.setCustomer(userService.getUserById(customerId));
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        // 3. Tính tổng & lưu item
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

        System.out.println("✅ [Order] Đơn hàng #" + savedOrder.getId() + " đã tạo thành công. Tổng: " + total);
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