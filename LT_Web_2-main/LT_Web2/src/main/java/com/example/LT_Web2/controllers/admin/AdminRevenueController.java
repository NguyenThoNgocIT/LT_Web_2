package com.example.LT_Web2.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.LT_Web2.services.OrderService;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/revenue")
public class AdminRevenueController {
    @Autowired
    private OrderService orderService;

    // type: day, week, month
    @GetMapping
    public ResponseEntity<Map<String, Object>> getRevenue(
            @RequestParam String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        Map<String, Object> result = orderService.getRevenueStatistics(type, from, to);
        return ResponseEntity.ok(result);
    }
}
