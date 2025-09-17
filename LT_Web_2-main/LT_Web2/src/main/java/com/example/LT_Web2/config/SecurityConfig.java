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
                .headers(headers -> headers.frameOptions(frame -> frame.disable())) // 👈 Cho phép H2 Console trong
                                                                                    // iframe
                .authorizeHttpRequests(auth -> auth
                        // Cho phép tạo user mới không cần login
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()

                        // cho phép xem user
                        .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()

                        // Cho phép cập nhật thông tin user (UPDATE)
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").permitAll()

                        // Cho phép xóa user (DELETE)
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").permitAll()

                        // Các API khác phải login
                        .requestMatchers("/api/**").authenticated()

                        // Public
                        .requestMatchers("/login", "/register", "/process-register", "/h2-console/**").permitAll()

                        // Web controller
                        .requestMatchers("/user/**", "/company/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                        // Những request khác cũng phải login
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
                // Bật Basic Auth cho API (Postman test)
                .httpBasic();

        return http.build();
    }
}
