package com.site.pine.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CookieUtil {

    private static final String VIEWER_COOKIE = "VIEWER_ID";

    public String getOrCreate(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        // 1. 기존 쿠키 찾기
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (VIEWER_COOKIE.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // 2. 없으면 새로 생성
        String uuid = UUID.randomUUID().toString();

        Cookie cookie = new Cookie(VIEWER_COOKIE, uuid);
        cookie.setHttpOnly(true);     // JS 접근 차단
        cookie.setPath("/");          // 전체 경로
        cookie.setMaxAge(60 * 60 * 24 * 365); // 1년
        // cookie.setSecure(true);    // HTTPS일 때만 (운영)

        response.addCookie(cookie);

        return uuid;
    }
}