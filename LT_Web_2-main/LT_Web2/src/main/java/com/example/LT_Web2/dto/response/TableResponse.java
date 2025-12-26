package com.example.LT_Web2.dto.response;

import lombok.Data;

@Data
public class TableResponse {

    private Long id;
    private String name;
    private String location;
    private String status;

    public TableResponse(com.example.LT_Web2.entity.Tables table) {
        if (table != null) {
            this.id = table.getId();
            this.name = table.getName();
            this.location = table.getLocation();
            this.status = table.getStatus() != null ? table.getStatus().name() : null;
        }
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getStatus() { return status; }
}