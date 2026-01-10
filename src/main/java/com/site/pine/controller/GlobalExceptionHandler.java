package com.site.pine.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // "잘못된 접근입니다" 같은 예상된 에러 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException e) {

        // [핵심 1] 스택 트레이스(e.printStackTrace)를 절대 찍지 마세요!
        // [핵심 2] 경고 로그를 한 줄만 남기거나, 아예 안 남깁니다.
         log.warn("비정상 접근 감지: {}", e.getMessage());

        // 클라이언트에게는 400 Bad Request와 짧은 메시지만 툭 던집니다.
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied() {
        return "redirect:/errorLogin";
    }

    /**
     * 2. 정적 리소스 파일 없음 (404 Not Found)
     * - js/map 파일, favicon, 이미지 등 없는 파일 요청 시 발생
     * * [관리 포인트]
     * - 이것도 서버 에러가 아니므로 error 레벨 로그 금지
     * - 어떤 파일을 찾으려다 실패했는지 경로만 warn으로 남김
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResourceFound(NoResourceFoundException e) {
        // 로그 예: "존재하지 않는 파일 요청: js/swiper-bundle.min.js.map"
        log.warn("존재하지 않는 파일 요청: {}", e.getResourcePath());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND) // 404 에러
                .build(); // 바디 없이 깔끔하게 리턴
    }

    // 진짜 서버 에러(NPE 등)만 스택 트레이스를 찍습니다.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleServerException(Exception e) {
        log.error("서버 내부 오류 발생", e); // 이건 우리가 고쳐야 하니까 다 찍음
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
