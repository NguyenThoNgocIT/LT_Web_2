package com.example.LT_Web2.controllers.user;

import com.example.LT_Web2.dto.request.ReservationRequest;
import com.example.LT_Web2.dto.response.ReservationResponse;
import com.example.LT_Web2.dto.response.TableResponse;
import com.example.LT_Web2.entity.Reservation;
import com.example.LT_Web2.entity.TableStatus;
import com.example.LT_Web2.entity.Tables;
import com.example.LT_Web2.entity.User;
import com.example.LT_Web2.services.ReservationService;
import com.example.LT_Web2.services.ProductService;
import com.example.LT_Web2.dto.response.ProductResponse;
import com.example.LT_Web2.entity.Product;
import com.example.LT_Web2.entity.ProductStatus;
import com.example.LT_Web2.services.TableService;
import com.example.LT_Web2.services.UserService;
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
    private final ProductService productService;
    private final UserService userService;

    @GetMapping("/tables/available")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<TableResponse>> getAvailableTables() {
        List<Tables> tables = tableService.findByStatus(TableStatus.AVAILABLE);
        List<TableResponse> response = tables.stream()
                .map(TableResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ROOT')")
    public ResponseEntity<User> getCurrentUser() {
        Long userId = getCurrentUserId();
        User user = userService.getUserById(userId); // ← Cần inject UserService
        return ResponseEntity.ok(user);
    }

    /**
     * Khách xem menu (danh sách sản phẩm có trạng thái AVAILABLE)
     * GET /api/user/menu
     * Public (không bắt buộc phải đăng nhập)
     */
    @GetMapping("/menu")
    public ResponseEntity<List<ProductResponse>> getMenu() {
        List<Product> products = productService.findByStatus(ProductStatus.AVAILABLE);
        List<ProductResponse> response = products.stream()
                .map(ProductResponse::new)
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