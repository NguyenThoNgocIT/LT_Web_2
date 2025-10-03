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
        System.out.println("Authorities: " + authorities); // Debug log

        boolean isAdmin = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (isAdmin) {
            System.out.println("Redirecting to /admin/dashboard for ADMIN");
            response.sendRedirect("/admin/dashboard");
        } else {
            boolean hasUserRole = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals("ROLE_USER"));
            if (hasUserRole) {
                System.out.println("Redirecting to /user/profile for USER");
                response.sendRedirect("/user/profile");
            } else {
                System.out.println("No valid role found, redirecting to /login?error=Unknown role");
                response.sendRedirect("/login?error=Unknown role");
            }
        }
    }
}