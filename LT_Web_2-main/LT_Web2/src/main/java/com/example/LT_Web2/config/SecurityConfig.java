package com.example.LT_Web2.config;

import com.example.LT_Web2.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // ✅ SỬA Ở ĐÂY: JwtAuthFilter là tham số, KHÔNG phải field
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN", "ROOT")
                        .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "ROOT")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json; charset=utf-8");
                            response.setCharacterEncoding("utf-8");
                            response.getWriter().write("""
                        {
                          "timestamp": "%s",
                          "status": 401,
                          "error": "Unauthorized",
                          "message": "Token không hợp lệ hoặc chưa cung cấp token",
                          "path": "%s"
                        }
                        """.formatted(
                                    java.time.Instant.now(),
                                    request.getRequestURI()
                            ));
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json; charset=utf-8");
                            response.setCharacterEncoding("utf-8");
                            response.getWriter().write("""
                        {
                          "timestamp": "%s",
                          "status": 403,
                          "error": "Forbidden",
                          "message": "Bạn không có quyền truy cập tài nguyên này",
                          "path": "%s"
                        }
                        """.formatted(
                                    java.time.Instant.now(),
                                    request.getRequestURI()
                            ));
                        })
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Cho phép origin cho cả localhost:3000 (FE cũ) và localhost:3001 (FE hiện tại).
        // Trong dev có thể mở rộng hoặc dùng allowedOriginPatterns để cho phép nhiều cổng.
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:3001"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        // Cho phép gửi cookie/authorization header nếu FE gửi credentials
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Áp dụng cho API path. Nếu muốn áp dụng toàn trang, đổi thành "/**".
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}