package com.example.LT_Web2.controllers.user;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import com.example.LT_Web2.dto.request.ReservationRequest;
import com.example.LT_Web2.dto.response.ReservationResponse;
import com.example.LT_Web2.entity.Reservation;
import com.example.LT_Web2.entity.User;
import com.example.LT_Web2.services.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/reservations")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class UserReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request) {
        Long userId = getCurrentUserId();
        Reservation reservation = reservationService.createReservation(request, userId);
        return ResponseEntity.ok(new ReservationResponse(reservation));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReservationResponse>> getMyReservations() {
        Long userId = getCurrentUserId();
        List<Reservation> reservations = reservationService.getReservationsByCustomer(userId);
        List<ReservationResponse> response = reservations.stream()
                .map(ReservationResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Reservation> cancelMyReservation(@PathVariable Long id) {
        // TODO: Kiểm tra reservation thuộc về user hiện tại
        return ResponseEntity.ok(reservationService.cancelReservation(id));
    }

    private Long getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User) {
            return ((User) auth.getPrincipal()).getId();
        }
        throw new IllegalStateException("User not authenticated");
    }
}