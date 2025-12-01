package com.site.pine.security;

import com.site.pine.dto.member.MemberDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;


import java.util.ArrayList;
import java.util.List;

public class AuthUtil {

    // @param member 로그인한 회원 객체

    public static void login(MemberDto member, HttpSession session) {

        List<GrantedAuthority> authorities = new ArrayList<>();

        // 0. 숫자 등급 기반 권한 부여
        if(member.getPosition() >= 0) authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        if(member.getPosition() >= 10) authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        if(member.getPosition() >= 100) authorities.add(new SimpleGrantedAuthority("ROLE_MASTER"));

        // 1. Authentication 객체 생성
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        member, // principal
                        null,   // credentials
                        authorities
                );

        // 2. SecurityContext에 인증 등록
        SecurityContextHolder.getContext().setAuthentication(auth);

        // 3. 세션에도 SecurityContext 등록
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
    }


     // 로그아웃 처리 (SecurityContext 제거)

    public static void logout(HttpSession session) {
        SecurityContextHolder.clearContext();
        session.invalidate();
    }
}

