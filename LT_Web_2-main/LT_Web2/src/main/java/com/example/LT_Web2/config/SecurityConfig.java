package com.example.LT_Web2.config;

import com.example.LT_Web2.services.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;
import java.util.List; // ← rất quan trọng
import com.example.LT_Web2.repository.UserRepository;

//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity(prePostEnabled = true)
//public class SecurityConfig {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Bean
//    public UserDetailsService userDetailsService() {
//        return (email) -> {
//            UseModel user = userRepository.findByEmail(email)
//                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
//            return user; // UseModel đã triển khai UserDetails
//        };
//    }
//
//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setUserDetailsService(userDetailsService());
//        provider.setPasswordEncoder(bCryptPasswordEncoder());
//        return provider;
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager() throws Exception {
//        return authentication -> authenticationProvider().authenticate(authentication);
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests((auth) -> auth
//                        .requestMatchers("/", "/login", "/register", "/process-register").permitAll()
//                        .requestMatchers("/api/auth/**").permitAll()
//                        .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
//                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
//                        .requestMatchers("/user/profile", "/user/profile/**", "/user/company/save").hasAnyRole("USER", "ADMIN")
//                        .requestMatchers("/admin/**").hasRole("ADMIN")
//                        .anyRequest().authenticated()
//                )
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)) // Thay bằng ALWAYS để test
//                .formLogin(form -> form
//                        .loginPage("/login")
//                        .successHandler(new CustomAuthenticationSuccessHandler())
//                        .failureUrl("/login?error")
//                        .permitAll()
//                )
//                .logout(logout -> logout
//                        .logoutSuccessUrl("/login?logout")
//                        .permitAll()
//                )
//                .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//
//    @Bean
//    public JwtAuthFilter jwtAuthFilter() {
//        return new JwtAuthFilter(userDetailsService(), jwtService());
//    }
//
//    @Bean
//    public JwtService jwtService() {
//        return new JwtService();
//    }
//}
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


    // --- UserDetailsService ---
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        return provider;
    }

    // ⚠️ Không cần AuthenticationManager bean nếu dùng đúng cách (Spring Boot 3+)
    // Nhưng nếu bạn cần, hãy inject nó đúng cách (xem bên dưới)

    // ✅ CHUỖI BẢO MẬT CHO WEB (SESSION-BASED)
    @Bean
    @Order(1)
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/", "/login","logout", "/register", "/process-register", "/user/**", "/admin/**")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/register", "/process-register").permitAll()
                        .requestMatchers("/user/profile", "/user/company/save").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(new CustomAuthenticationSuccessHandler())
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfiguration() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:3000")); // React dev server
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // nếu dùng cookie
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    // ✅ CHUỖI BẢO MẬT CHO API (JWT - STATELESS)
    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http,
                                                      JwtAuthFilter jwtAuthFilter) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfiguration()))
                .securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling( ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->{
                            // phần xử lí chưa xác thực nó sẽ log ra 401
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
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(userDetailsService(), jwtService());
    }

    @Bean
    public JwtService jwtService() {
        return new JwtService();
    }
}