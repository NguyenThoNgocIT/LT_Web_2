package com.example.LT_Web2.repository;

import com.example.LT_Web2.entity.Reservation;
import com.example.LT_Web2.entity.ReservationStatus;
import com.example.LT_Web2.entity.Tables;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByCustomerId(Long customerId);

    List<Reservation> findByStatus(ReservationStatus status);

    // Tìm reservation theo bàn và trạng thái
    List<Reservation> findByTableAndStatus(Tables table, ReservationStatus status);

    // Tìm reservation đang hoạt động của user (PENDING hoặc CONFIRMED)
    @Query("SELECT r FROM Reservation r WHERE r.customer.id = :customerId " +
            "AND (r.status = com.example.LT_Web2.entity.ReservationStatus.PENDING " +
            "OR r.status = com.example.LT_Web2.entity.ReservationStatus.CONFIRMED) " +
            "ORDER BY r.reservationTime DESC")
    List<Reservation> findActiveReservationsByCustomer(@Param("customerId") Long customerId);

    // Tìm reservation quá giờ (dùng cho No-show)
    @Query("SELECT r FROM Reservation r WHERE r.reservationTime <= :now " +
            "AND r.status = com.example.LT_Web2.entity.ReservationStatus.CONFIRMED")
    List<Reservation> findConfirmedOverdue(@Param("now") LocalDateTime now);
}
