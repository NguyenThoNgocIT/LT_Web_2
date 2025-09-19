package com.example.LT_Web2.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

// Đây là Entity cho Company
// Tầng Model (hoặc Entity) đại diện cho bảng Company trong DB
@Entity
@Table(name = "Company")
public class CompanyModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID tự tăng, là khóa chính

    @Column(nullable = false)
    private String companyName; // Tên công ty, không được null

    // Một công ty có nhiều user (1-N)
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UseModel> users = new ArrayList<>();
    // mappedBy = "company" nghĩa là bên UseModel có field company liên kết ngược
    // cascade = CascadeType.ALL: các hành động (save, delete) propagate xuống user
    // orphanRemoval = true: xóa user nếu không còn liên kết

    // Getters và Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public List<UseModel> getUsers() {
        return users;
    }

    public void setUsers(List<UseModel> users) {
        this.users = users;
    }
}
