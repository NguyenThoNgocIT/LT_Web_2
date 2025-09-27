package com.example.LT_Web2.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


import java.io.IOException;
import java.util.Collection;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // Lấy các authorities (roles) của user
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean isAdmin = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));  // Spring Security lưu role với prefix "ROLE_"

        if (isAdmin) {
            response.sendRedirect("/admin/dashboard");  // Redirect admin về dashboard (có modal add user)
        } else {
            response.sendRedirect("/user/profile");  // Redirect user thường về trang user (thay đổi nếu cần)
        }
    }
}
