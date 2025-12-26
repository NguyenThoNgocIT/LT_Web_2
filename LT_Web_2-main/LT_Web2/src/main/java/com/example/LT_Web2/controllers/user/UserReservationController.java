package com.example.LT_Web2.controllers.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import com.example.LT_Web2.dto.request.ReservationRequest;
import com.example.LT_Web2.dto.response.ReservationResponse;
import com.example.LT_Web2.entity.Reservation;
import com.example.LT_Web2.entity.User;
import com.example.LT_Web2.services.ReservationService;
import com.example.LT_Web2.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/reservations")
@PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
@RequiredArgsConstructor
public class UserReservationController {

    private final ReservationService reservationService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request,
            HttpServletRequest httpRequest) {
        System.out.println(" [ReservationController] POST /api/user/reservations");
        System.out.println("   Request body: tableId=" + request.getTableId() +
                ", time=" + request.getReservationTime() +
                ", note=" + request.getNote());

        Long userId = getCurrentUserId(httpRequest);
        System.out.println("   User ID: " + userId);

        Reservation reservation = reservationService.createReservation(request, userId);
        System.out.println(" [ReservationController] Reservation created: #" + reservation.getId());
        return ResponseEntity.ok(new ReservationResponse(reservation));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReservationResponse>> getMyReservations(HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
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

    private Long getCurrentUserId(HttpServletRequest request) {
        // Try to get from JWT token first
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Long userId = jwtService.extractUserId(token);
                if (userId != null) {
                    System.out.println(" [Reservation] Extracted userId from JWT: " + userId);
                    return userId;
                }
            } catch (Exception e) {
                System.err.println(" [Reservation] Cannot extract userId from JWT: " + e.getMessage());
            }
        }

        // Fallback to SecurityContext
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User) {
            User user = (User) auth.getPrincipal();
            System.out.println(" [Reservation] Got userId from SecurityContext: " + user.getId());
            return user.getId();
        }

        // Log chi tiết để debug
        System.err.println(" [Reservation] Cannot get user ID. Auth: " + auth);
        if (auth != null) {
            System.err.println("Principal type: " + auth.getPrincipal().getClass().getName());
        }
        throw new IllegalStateException("User not authenticated or principal is not User type");
    }
}