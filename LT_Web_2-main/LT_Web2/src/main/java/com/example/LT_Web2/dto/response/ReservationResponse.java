package com.example.LT_Web2.dto.response;

import com.example.LT_Web2.entity.ReservationStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class ReservationResponse {

    private Long id;
    private TableResponse table;
    private UserResponse customer;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime reservationTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;
    private String status;

    // Constructor từ entity
    public ReservationResponse(com.example.LT_Web2.entity.Reservation reservation) {
        this.id = reservation.getId();
        this.table = new TableResponse(reservation.getTable());
        this.customer = new UserResponse(reservation.getCustomer());
        this.reservationTime = reservation.getReservationTime();
        this.createdAt = reservation.getCreatedAt();
        this.status = reservation.getStatus() != null ? reservation.getStatus().name() : null;
    }

    // Getters
    public Long getId() { return id; }
    public TableResponse getTable() { return table; }
    public UserResponse getCustomer() { return customer; }
    public LocalDateTime getReservationTime() { return reservationTime; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getStatus() { return status; }
}