package com.example.LT_Web2.services;

import com.example.LT_Web2.dto.request.ReservationRequest;
import com.example.LT_Web2.entity.Reservation;

import java.util.List;

public interface ReservationService {
    Reservation confirmReservation(Long id);
    Reservation cancelReservation(Long id);
    List<Reservation> getAllReservations();
    List<Reservation> getReservationsByCustomer(Long customerId);
    void handleNoShowReservations(); // Tự động xử lý No-show
    Reservation createReservation(ReservationRequest request, Long customerId);

}
