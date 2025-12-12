package com.site.pine.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    private static final String[] authenticatedUserUrl = {
            // 로그인 유저 접근 url 설정
            "/GoMypage",
    };

    private static final String[] anonymousUserUrl = {
            // 비로그인 유저 접근 url 설정
            "/login/**",         // 네이버 로그인 redirect + callback
            "/",
            "/index",
            "/css/**",
            "/js/**",
            "/img/**",
            "/favicon.ico",
            "/auth/naver/login",
            "/naver/callback",
            "/goJoin",
            "/insertMember",
            "/jointerms",
            "/join",
            "/oauth2/**",
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

                        .requestMatchers(anonymousUserUrl).permitAll()
                        .requestMatchers(authenticatedUserUrl).authenticated()

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().permitAll()
                )
//                .exceptionHandling(ex -> {})

                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .defaultSuccessUrl("/googleLoginSuccess", true) // 로그인 성공 후 이동
                        .failureUrl("/login?error") // 로그인 실패 시
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")              // 기본값, POST 요청
                        .logoutSuccessUrl("/")             // 로그아웃 후 이동할 페이지
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }




}
