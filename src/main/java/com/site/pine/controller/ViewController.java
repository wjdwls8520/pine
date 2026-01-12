package com.site.pine.controller;

import com.site.pine.cookie.CookieUtil;
import com.site.pine.dto.member.MemberDto;
import com.site.pine.service.ShortsService;
import com.site.pine.service.ViewService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/view")
@RequiredArgsConstructor
public class ViewController {

    private final CookieUtil cookieUtil;

    private final ViewService vs;
    private final ShortsService ss;

    @PostMapping("/groupviewcount/{targetId}")
    public HashMap<String, Object> groupViewCount(
            @PathVariable Long targetId,
            @AuthenticationPrincipal MemberDto memberdto,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        HashMap<String, Object> result = new HashMap<>();

        Long isMember = (memberdto != null) ? memberdto.getId() : null;
        // 쿠키유틸파일에서 쿠키생성하기
        String viewerCookie = cookieUtil.getOrCreate(request, response);

        try {
            HashMap<String, Object> viewObject = vs.addGroupViewCount(targetId, isMember, viewerCookie);
            result.put("allViewCount", viewObject.get("allViewCount"));
            result.put("todayViewCount", viewObject.get("todayViewCount"));
        } catch (EntityNotFoundException e) {
            result.put("msg", e.getMessage());
            return result;
        }


        result.put("msg", "success");
        return result;
    }

    @GetMapping("/groupcalculateCompare/{targetId}")
    public HashMap<String, Object> calculateCompare(@PathVariable("targetId") Long targetId) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("viewCompareResult", vs.groupCalculateCompare(targetId));
        return result;
    }

    // 쇼츠 재생수 증가
    @PostMapping("/shorts/{targetId}")
    public ResponseEntity<String> increaseShortsView(
            @PathVariable Long targetId,
            @AuthenticationPrincipal MemberDto memberDto,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        // 로그인 여부 확인
        Long memberId = (memberDto != null) ? memberDto.getId() : null;

        // 쿠키 확인 및 발급 (비회원 식별용)
        String cookie = cookieUtil.getOrCreate(request, response);

        // 비동기 서비스 호출
        // 결과(성공/실패)를 기다리지 않고 실행만 시키고 넘어갑니다.
        ss.countView(targetId, memberId, cookie);

        return ResponseEntity.ok("counted");
    }
}
