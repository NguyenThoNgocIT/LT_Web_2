package com.example.LT_Web2.dto.response;

public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;

    public UserResponse(com.example.LT_Web2.entity.User user) {
        if (user != null) {
            this.id = user.getId();
            this.name = user.getName();
            this.email = user.getEmail();
            this.phone = user.getPhone();
            // ❌ Không bao gồm: password, roles, authorities
        }
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
}