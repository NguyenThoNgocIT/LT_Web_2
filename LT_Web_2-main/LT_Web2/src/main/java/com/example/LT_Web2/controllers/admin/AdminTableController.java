package com.example.LT_Web2.controllers.admin;

import com.example.LT_Web2.entity.TableStatus;
import com.example.LT_Web2.entity.Tables;
import com.example.LT_Web2.services.TableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/tables")
@RequiredArgsConstructor
public class AdminTableController {

    private final TableService tableService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
    public ResponseEntity<List<Tables>> getAllTables() {
        return ResponseEntity.ok(tableService.findAll());
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
    public ResponseEntity<List<Tables>> getTablesByStatus(@PathVariable String status) {
        TableStatus tableStatus = TableStatus.valueOf(status.toUpperCase());
        return ResponseEntity.ok(tableService.findByStatus(tableStatus));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
    public ResponseEntity<List<Tables>> createTables(@Valid @RequestBody List<Tables> tables) {
        return ResponseEntity.ok(tableService.saveAll(tables));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
    public ResponseEntity<Tables> updateTable(@PathVariable Long id, @Valid @RequestBody Tables tableUpdate) {
        Tables existing = tableService.findById(id);
        existing.setName(tableUpdate.getName());
        existing.setLocation(tableUpdate.getLocation());
        return ResponseEntity.ok(tableService.save(existing));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
    public ResponseEntity<Tables> updateTableStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String statusStr = request.get("status");
        if (statusStr == null || statusStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Trường 'status' không được để trống");
        }
        TableStatus newStatus = TableStatus.valueOf(statusStr.toUpperCase());
        return ResponseEntity.ok(tableService.updateStatus(id, newStatus));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        tableService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}