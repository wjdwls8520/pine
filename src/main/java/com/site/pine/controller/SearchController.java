package com.site.pine.controller;

import com.site.pine.dto.group.GroupSelectDto;
import com.site.pine.dto.member.MemberDto;
import com.site.pine.repository.group.GroupContentsRepository;
import com.site.pine.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;
    private final GroupContentsRepository groupContentsRepository; // [추가]

    // 1. 검색 화면 (JSP 반환)
    @GetMapping("/search")
    public String searchPage(
            @RequestParam(value = "keyword", required = false) String keyword,
            @AuthenticationPrincipal MemberDto mdto, // 로그인 정보 확인
            Model model
    ) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return "redirect:/";
        }

        // [추가] 로그인 유저라면 가입한 그룹 목록을 가져와서 모델에 담음
        if (mdto != null) {
            List<GroupSelectDto> myGroups = groupContentsRepository.findJoinedGroups(mdto.getId());
            model.addAttribute("myGroups", myGroups); // JSP에서 탭 보여주기/숨기기 판단용
        }

        model.addAttribute("keyword", keyword);
        return "search/searchResult";
    }

    // 2. [API] 데이터 요청
    @GetMapping("/api/search")
    @ResponseBody
    public Map<String, Object> searchData(
            @RequestParam("type") String type,
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "sort", defaultValue = "relevance") String sort,
            @RequestParam(value = "period", defaultValue = "all") String period,
            @RequestParam(value = "groupId", required = false) Long groupId, // [추가] 특정 그룹 필터링
            @AuthenticationPrincipal MemberDto mdto
    ) {
        Long memberId = (mdto != null) ? mdto.getId() : null;

        // Service로 groupId도 전달
        return searchService.searchByType(type, keyword, page, sort, period, memberId, groupId);
    }
}