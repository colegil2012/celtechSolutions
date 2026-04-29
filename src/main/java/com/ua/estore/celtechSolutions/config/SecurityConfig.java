package com.ua.estore.celtechSolutions.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Public-site security configuration.
 * <p>
 * The landing page is fully public; we keep CSRF enabled so the contact form
 * gets protection out of the box (Thymeleaf adds the token automatically).
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/about", "/contact", "/links",
                                "/css/**", "/js/**", "/img/**", "/favicon.ico",
                                "/webjars/**", "/error")
                        .permitAll()
                        .anyRequest().authenticated()
                )
                // Keep CSRF on for the contact form. Thymeleaf injects the token automatically.
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());
        return http.build();
    }
}