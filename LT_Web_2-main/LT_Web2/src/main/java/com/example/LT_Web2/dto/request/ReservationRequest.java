package com.example.LT_Web2.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ReservationRequest {

    @NotNull(message = "Bắt buộc chọn bàn")
    private Long tableId;

    @Future(message = "Thời gian đặt bàn phải ở tương lai")
    @NotNull(message = "Bắt buộc chọn thời gian")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime reservationTime;

    private String note; // Ghi chú: "4 người, gần cửa sổ"

    // Getters & Setters
    public Long getTableId() { return tableId; }
    public void setTableId(Long tableId) { this.tableId = tableId; }

    public LocalDateTime getReservationTime() { return reservationTime; }
    public void setReservationTime(LocalDateTime reservationTime) { this.reservationTime = reservationTime; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}