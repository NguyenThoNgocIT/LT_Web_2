package com.example.LT_Web2.models;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

// Đây là Entity cho User
// Tầng Model (Entity) đại diện cho bảng UserModel trong DB
// Implement UserDetails để Spring Security quản lý user
@Entity
@Table(name = "UserModel")
public class UseModel implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID tự tăng, là khóa chính

    @Column(nullable = false)
    private String name; // Tên user, không null

    @Column(nullable = false, unique = true)
    private String email; // Email, không null, duy nhất

    private String phone; // Số điện thoại, optional

    @Column(nullable = false)
    private String password; // Mật khẩu, mã hóa bằng BCrypt

    // Set roles cho user
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();
    // Lưu role của user, ví dụ USER, ADMIN

    // Liên kết ManyToOne với Company
    @ManyToOne
    @JoinColumn(name = "company_id", nullable = true)
    private CompanyModel company;
    // Một user chỉ thuộc về một công ty

    // version để JPA handle optimistic locking
    @Version
    private Integer version;

    // Getters và Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public CompanyModel getCompany() {
        return company;
    }

    public void setCompany(CompanyModel company) {
        this.company = company;
    }

    // UserDetails methods cho Spring Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
