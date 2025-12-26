package com.example.LT_Web2.dto.request;

import jakarta.validation.constraints.NotBlank;

public class TableStatusUpdateRequest {
    @NotBlank(message = "Trạng thái không được để trống")
    private String status; // "AVAILABLE", "OCCUPIED", ...

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
