package com.site.pine.controller;

import com.site.pine.dto.like.LikeReqDto;
import com.site.pine.dto.like.LikeResDto;
import com.site.pine.dto.member.MemberDto;
import com.site.pine.service.LikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/like")
public class LikesController {

    private final LikesService ls;

    @PostMapping
    public ResponseEntity<LikeResDto> toggleLike(
            @RequestBody LikeReqDto requestDto,
            @AuthenticationPrincipal MemberDto mdto
    ) {
        // 1. 로그인 안 된 경우 처리 (Security가 막아주겠지만 이중 체크)
        if (mdto == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        // 2. 현재 로그인한 멤버 ID 추출
        Long memberId = mdto.getId();

        // 3. 서비스 호출 (좋아요 토글 로직 실행)
        LikeResDto response = ls.toggleLike(requestDto, memberId);

        // 4. 결과 리턴 (JSON)
        return ResponseEntity.ok(response);
    }

}

