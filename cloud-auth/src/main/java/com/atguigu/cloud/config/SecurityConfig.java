package com.atguigu.cloud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 先关 CSRF，避免 POST/PUT 被 403
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 放行这些接口（按需改）
                        .requestMatchers("/oauth/**", "/login/**", "/actuator/health").permitAll()
                        // 其他都要认证
                        .anyRequest().authenticated()
                )
                // 先用最简单的方式，避免浏览器跳登录页（接口更直观）
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}