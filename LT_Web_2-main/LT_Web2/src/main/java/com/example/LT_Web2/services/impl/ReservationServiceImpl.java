package com.example.LT_Web2.services.impl;

import com.example.LT_Web2.dto.request.ReservationRequest;
import com.example.LT_Web2.entity.*;
import com.example.LT_Web2.exception.BusinessException;
import com.example.LT_Web2.exception.ResourceNotFoundException;
import com.example.LT_Web2.repository.ReservationRepository;
import com.example.LT_Web2.repository.TableRepository;
import com.example.LT_Web2.repository.UserRepository;
import com.example.LT_Web2.services.ReservationService;
import com.example.LT_Web2.services.TableService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final TableService tableService;
    private final TableRepository tableRepository;
    private final UserRepository userRepository; // ← Thêm dòng này

    @Override
    public Reservation createReservation(ReservationRequest request, Long customerId) {
        System.out.println("🔔 [Reservation] Creating reservation...");
        System.out.println("   Table ID: " + request.getTableId());
        System.out.println("   Customer ID: " + customerId);
        System.out.println("   Reservation Time: " + request.getReservationTime());
        System.out.println("   Note: " + request.getNote());

        // 1. Kiểm tra thời gian
        if (request.getReservationTime().isBefore(LocalDateTime.now())) {
            System.err.println("❌ [Reservation] Time is in the past");
            throw new BusinessException("Thời gian đặt bàn phải lớn hơn thời gian hiện tại");
        }

        // 2. Load Table và User
        Tables table = tableRepository.findById(request.getTableId())
                .orElseThrow(() -> {
                    System.err.println("❌ [Reservation] Table not found: " + request.getTableId());
                    return new ResourceNotFoundException("Bàn không tồn tại");
                });

        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> {
                    System.err.println("❌ [Reservation] Customer not found: " + customerId);
                    return new ResourceNotFoundException("Khách hàng không tồn tại");
                });

        System.out.println("✅ [Reservation] Table found: " + table.getName() + " (status: " + table.getStatus() + ")");
        System.out.println("✅ [Reservation] Customer found: " + customer.getName());

        // 3. Kiểm tra bàn có sẵn không
        if (table.getStatus() != TableStatus.AVAILABLE) {
            System.err.println("❌ [Reservation] Table is not available: " + table.getStatus());
            throw new BusinessException("Bàn này hiện không khả dụng để đặt trước");
        }

        // 4. Tạo reservation
        Reservation reservation = new Reservation();
        reservation.setTable(table);
        reservation.setCustomer(customer);
        reservation.setReservationTime(request.getReservationTime());
        reservation.setNote(request.getNote());
        reservation.setStatus(ReservationStatus.PENDING);

        Reservation saved = reservationRepository.save(reservation);
        System.out.println("✅ [Reservation] Reservation created successfully: #" + saved.getId());

        // 5. ✅ Cập nhật trạng thái bàn sang RESERVED ngay lập tức
        tableService.updateStatus(table.getId(), TableStatus.RESERVED);
        System.out.println("✅ [Reservation] Table #" + table.getId() + " updated to RESERVED");

        return saved;
    }

    @Override
    public Reservation confirmReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đặt bàn"));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new BusinessException("Chỉ có thể xác nhận đặt bàn ở trạng thái 'Chờ xác nhận'");
        }

        // Xác nhận → chuyển bàn sang RESERVED (ngay lập tức hoặc khi gần đến giờ)
        reservation.setStatus(ReservationStatus.CONFIRMED);
        Reservation saved = reservationRepository.save(reservation);

        // ✅ Cập nhật trạng thái bàn → RESERVED
        tableService.updateStatus(reservation.getTable().getId(), TableStatus.RESERVED);

        return saved;
    }

    @Override
    public Reservation cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đặt bàn"));

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new BusinessException("Đặt bàn này đã bị hủy");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        Reservation saved = reservationRepository.save(reservation);

        // Nếu bàn đang RESERVED → trả về AVAILABLE
        if (reservation.getTable().getStatus() == TableStatus.RESERVED) {
            tableService.updateStatus(reservation.getTable().getId(), TableStatus.AVAILABLE);
        }

        return saved;
    }

    @Override
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Override
    public List<Reservation> getReservationsByCustomer(Long customerId) {
        return reservationRepository.findByCustomerId(customerId);
    }

    // 🕒 Xử lý No-show: chạy định kỳ (dùng @Scheduled)
    @Override
    public void handleNoShowReservations() {
        LocalDateTime now = LocalDateTime.now();
        // Tìm các reservation đã quá giờ + chưa check-in
        List<Reservation> overdue = reservationRepository.findConfirmedOverdue(now);

        for (Reservation r : overdue) {
            // Chuyển trạng thái reservation → NO_SHOW
            r.setStatus(ReservationStatus.NO_SHOW);
            reservationRepository.save(r);

            // Trả bàn về AVAILABLE
            if (r.getTable().getStatus() == TableStatus.RESERVED) {
                tableService.updateStatus(r.getTable().getId(), TableStatus.AVAILABLE);
            }
        }
    }

    @Component
    @RequiredArgsConstructor
    public class ReservationScheduler {
        private final ReservationService reservationService;

        @Scheduled(fixedRate = 90000)
        public void handleNoShow() {
            reservationService.handleNoShowReservations();
        }
    }
}
