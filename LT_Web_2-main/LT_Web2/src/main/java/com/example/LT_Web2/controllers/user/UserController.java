package com.example.LT_Web2.controllers.user;

import com.example.LT_Web2.dto.request.ReservationRequest;
import com.example.LT_Web2.dto.response.ReservationResponse;
import com.example.LT_Web2.dto.response.TableResponse;
import com.example.LT_Web2.entity.Reservation;
import com.example.LT_Web2.entity.TableStatus;
import com.example.LT_Web2.entity.Tables;
import com.example.LT_Web2.entity.User;
import com.example.LT_Web2.services.ReservationService;
import com.example.LT_Web2.services.TableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final TableService tableService;
    private final ReservationService reservationService;

    @GetMapping("/tables/available")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<TableResponse>> getAvailableTables() {
        List<Tables> tables = tableService.findByStatus(TableStatus.AVAILABLE);
        List<TableResponse> response = tables.stream()
                .map(TableResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reservations")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request) {
        Long userId = getCurrentUserId();
        var reservation = reservationService.createReservation(request, userId);
        return ResponseEntity.ok(new ReservationResponse(reservation));
    }

    private Long getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User) {
            return ((User) auth.getPrincipal()).getId();
        }
        throw new IllegalStateException("User not authenticated");
    }
    @GetMapping("/reservations")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ReservationResponse>> getMyReservations() {
        Long userId = getCurrentUserId();
        List<Reservation> reservations = reservationService.getReservationsByCustomer(userId);
        List<ReservationResponse> response = reservations.stream()
                .map(ReservationResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
/// test post man oke r