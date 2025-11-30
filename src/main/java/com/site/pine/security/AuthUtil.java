package com.site.pine.security;

import com.site.pine.dto.member.MemberDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;


import java.util.List;

public class AuthUtil {

    // @param member 로그인한 회원 객체

    public static void login(MemberDto member, HttpSession session) {
        // 1. Authentication 객체 생성
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        member, // principal
                        null,   // credentials
                        List.of(new SimpleGrantedAuthority("ROLE_USER")) // 권한
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

