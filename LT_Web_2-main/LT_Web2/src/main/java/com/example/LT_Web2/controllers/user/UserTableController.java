package com.example.LT_Web2.controllers.user;

import java.util.List;
import java.util.stream.Collectors;
import com.example.LT_Web2.dto.response.TableResponse;
import com.example.LT_Web2.entity.TableStatus;
import com.example.LT_Web2.entity.Tables;
import com.example.LT_Web2.services.TableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/user/tables")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class UserTableController {

    private final TableService tableService;

    @GetMapping("/available")
    public ResponseEntity<List<TableResponse>> getAvailableTables() {
        List<Tables> tables = tableService.findByStatus(TableStatus.AVAILABLE);
        List<TableResponse> response = tables.stream()
                .map(TableResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
