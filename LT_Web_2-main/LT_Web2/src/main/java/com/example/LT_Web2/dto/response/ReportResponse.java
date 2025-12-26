package com.example.LT_Web2.dto.response;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class ReportResponse {
    private long totalCustomers;
    private BigDecimal totalRevenue;
    private String period; // "Today", "This week", etc.

    public ReportResponse(long totalCustomers, BigDecimal totalRevenue, String period) {
        this.totalCustomers = totalCustomers;
        this.totalRevenue = totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
        this.period = period;
    }
}
