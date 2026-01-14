package com.site.pine.controller;

import com.site.pine.dto.member.MemberDto;
import com.site.pine.dto.mypage.MyGroupListDto;
import com.site.pine.dto.mypage.MyPostListDto;
import com.site.pine.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    /**
     * 마이페이지 메인 화면으로 이동
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mypage")
    public String mypage(@AuthenticationPrincipal MemberDto mdto, Model model) {
        if (mdto == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("member", mdto);
        return "member/mypage";
    }

    /**
     * 내가 쓴 글 리스트 JSON 반환
     * 파라미터: page, size, category(옵션)
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mypage/api/posts")
    public ResponseEntity<Map<String, Object>> getMyPosts(
            @AuthenticationPrincipal MemberDto mdto,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "category", required = false) Integer category
    ) {
        if (mdto == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "로그인이 필요합니다.");
            return ResponseEntity.status(401).body(errorResponse);
        }

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<MyPostListDto> postPage = myPageService.getMyPosts(mdto.getId(), pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("content", postPage.getContent());
            response.put("totalPages", postPage.getTotalPages());
            response.put("totalElements", postPage.getTotalElements());
            response.put("currentPage", postPage.getNumber());
            response.put("size", postPage.getSize());
            response.put("hasNext", postPage.hasNext());
            response.put("hasPrevious", postPage.hasPrevious());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("내가 쓴 글 조회 중 오류 발생", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "글 조회 중 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * 내가 댓글 단 글 리스트 JSON 반환
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mypage/api/comments")
    public ResponseEntity<Map<String, Object>> getMyCommentedPosts(
            @AuthenticationPrincipal MemberDto mdto,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        if (mdto == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "로그인이 필요합니다.");
            return ResponseEntity.status(401).body(errorResponse);
        }

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<MyPostListDto> postPage = myPageService.getMyCommentedPosts(mdto.getId(), pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("content", postPage.getContent());
            response.put("totalPages", postPage.getTotalPages());
            response.put("totalElements", postPage.getTotalElements());
            response.put("currentPage", postPage.getNumber());
            response.put("size", postPage.getSize());
            response.put("hasNext", postPage.hasNext());
            response.put("hasPrevious", postPage.hasPrevious());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("내가 댓글 단 글 조회 중 오류 발생", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "댓글 단 글 조회 중 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * 좋아요 한 글 리스트 JSON 반환
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mypage/api/likes")
    public ResponseEntity<Map<String, Object>> getMyLikedPosts(
            @AuthenticationPrincipal MemberDto mdto,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        if (mdto == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "로그인이 필요합니다.");
            return ResponseEntity.status(401).body(errorResponse);
        }

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<MyPostListDto> postPage = myPageService.getMyLikedPosts(mdto.getId(), pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("content", postPage.getContent());
            response.put("totalPages", postPage.getTotalPages());
            response.put("totalElements", postPage.getTotalElements());
            response.put("currentPage", postPage.getNumber());
            response.put("size", postPage.getSize());
            response.put("hasNext", postPage.hasNext());
            response.put("hasPrevious", postPage.hasPrevious());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("좋아요 한 글 조회 중 오류 발생", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "좋아요 한 글 조회 중 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * 내 그룹 리스트 JSON 반환
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mypage/api/groups")
    public ResponseEntity<Map<String, Object>> getMyGroups(
            @AuthenticationPrincipal MemberDto mdto,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        if (mdto == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "로그인이 필요합니다.");
            return ResponseEntity.status(401).body(errorResponse);
        }

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<MyGroupListDto> groupPage = myPageService.getMyGroups(mdto.getId(), pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("content", groupPage.getContent());
            response.put("totalPages", groupPage.getTotalPages());
            response.put("totalElements", groupPage.getTotalElements());
            response.put("currentPage", groupPage.getNumber());
            response.put("size", groupPage.getSize());
            response.put("hasNext", groupPage.hasNext());
            response.put("hasPrevious", groupPage.hasPrevious());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("내 그룹 조회 중 오류 발생", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "그룹 조회 중 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}

