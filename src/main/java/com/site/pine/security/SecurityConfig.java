package com.site.pine.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }



    private static final String[] authenticatedUserUrl = {
            "/**"
    };

    private static final String[] permitAllUrl = {
            "/**",
//            "/",
//            "/login/**",         // 네이버 로그인 redirect + callback
//            "/css/**", "/js/**", "/img/**"
    };


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // POST/PUT/DELETE 같은 상태 변경 요청에 대해 CSRF 토큰 검증
                .csrf(csrf -> csrf.disable()) // 6.x 이상 방식; // 개발 단계에서만
                .cors(cors -> {})
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

                // requestMatchers 설정값에 따라 접근 권한 제어
                // 해당 config에서는 anonymousUserUrl(비로그인 유저) 와 authenticatedUserUrl(로그인 유저) 로 나누어 적용함
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/master/**").hasRole("MASTER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers(authenticatedUserUrl).permitAll()
                        .requestMatchers(permitAllUrl).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().permitAll()
                )
                .exceptionHandling(ex -> {});

        return http.build();
    }




}
