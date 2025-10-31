package com.example.LT_Web2.config;

import com.example.LT_Web2.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Autowired
    public JwtAuthFilter(UserDetailsService userDetailsService, JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String username = null;

        System.out.println(" [JWT Filter] " + request.getMethod() + " " + request.getRequestURI());
        System.out.println(" [JWT Filter] Authorization header: "
                + (authHeader != null ? authHeader.substring(0, Math.min(30, authHeader.length())) + "..." : "NULL"));

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                username = jwtService.extractUsername(jwt);
                System.out.println(" [JWT Filter] Extracted username: " + username);
            } catch (Exception e) {
                System.err.println(" [JWT Filter] Error extracting username from JWT: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println(" [JWT Filter] No valid Authorization header");
        }

        // Nếu có username và chưa xác thực
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                System.out.println("[JWT Filter] Loaded user: " + username);
                System.out.println(" [JWT Filter] User authorities: " + userDetails.getAuthorities());

                //  Kiểm tra token hợp lệ
                if (jwtService.validateToken(jwt, userDetails)) {
                    // Tạo authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println(
                            " [JWT Filter] JWT authenticated user: " + username + " with roles: "
                                    + userDetails.getAuthorities());
                } else {
                    System.err.println(" [JWT Filter] JWT token invalid for user: " + username);
                }
            } catch (Exception e) {
                System.err.println(" [JWT Filter] Error during JWT authentication: " + e.getMessage());
                e.printStackTrace();
            }
        } else if (username == null) {
            System.err.println(" [JWT Filter] Username is null, skipping authentication");
        } else {
            System.out.println(" [JWT Filter] User already authenticated: "
                    + SecurityContextHolder.getContext().getAuthentication().getName());
        }

        chain.doFilter(request, response);
    }
}