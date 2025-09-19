package com.example.LT_Web2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// Báo Spring đây là file cấu hình, nó sẽ đọc khi khởi động app.
@Configuration
public class SecurityConfig {

    @Bean
    UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Tắt CSRF để Postman gửi JSON
                .headers(headers -> headers.frameOptions(frame -> frame.disable())) // Cho phép H2 Console trong iframe
                .authorizeHttpRequests(auth -> auth
                        // ==== USERS API ====
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()       // tạo user (signup)
                        .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()      // xem user
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").permitAll()      // cập nhật user
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").permitAll()   // xóa user

                        // ==== COMPANIES API ====
                        .requestMatchers(HttpMethod.GET, "/api/companies/**").permitAll()        // xem công ty
                        .requestMatchers(HttpMethod.POST, "/api/companies").authenticated()      // tạo công ty
                        .requestMatchers(HttpMethod.PUT, "/api/companies/**").authenticated()    // sửa công ty
                        .requestMatchers(HttpMethod.DELETE, "/api/companies/**").authenticated() // xóa công ty

                        // Các API khác trong /api/** phải login
                        .requestMatchers("/api/**").authenticated()

                        // Public cho login, register, h2-console
                        .requestMatchers("/login", "/register", "/process-register", "/h2-console/**").permitAll()

                        // Web Controller: chỉ user/admin mới vào
                        .requestMatchers("/user/**", "/company/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                        // Những request còn lại cũng phải login
                        .anyRequest().authenticated())

                // Cho phép login form cho web
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/user/add", true)
                        .permitAll())
                // Cho phép logout
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                // Bật Basic Auth cho API (test Postman)
                .httpBasic();

        return http.build();
    }
}
