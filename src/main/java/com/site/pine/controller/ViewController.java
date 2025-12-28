package com.site.pine.controller;

import com.site.pine.cookie.CookieUtil;
import com.site.pine.dto.ViewReqDto;
import com.site.pine.dto.member.MemberDto;
import com.site.pine.enums.PageType;
import com.site.pine.service.ViewService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/view")
@RequiredArgsConstructor
public class ViewController {

    private final CookieUtil cookieUtil;

    private final ViewService vs;

    @PostMapping("/viewcount/{targetId}")
    public HashMap<String, Object> groupViewCount(
            @PathVariable Long targetId,
            @AuthenticationPrincipal MemberDto memberdto,
            @RequestBody ViewReqDto viewReqDto,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        HashMap<String, Object> result = new HashMap<>();

        Long isMember = (memberdto != null) ? memberdto.getId() : null;
        // 쿠키유틸파일에서 쿠키생성하기
        String viewerCookie = cookieUtil.getOrCreate(request, response);

        try {
            HashMap<String, Object> viewObject = vs.addViewCount(targetId, viewReqDto.pageType(), isMember, viewerCookie);
            result.put("allViewCount", viewObject.get("allViewCount"));
            if(viewReqDto.pageType().equals(PageType.GROUP)) result.put("todayViewCount", viewObject.get("todayViewCount"));
        } catch (EntityNotFoundException e) {
            result.put("msg", e.getMessage());
            return result;
        }


        result.put("msg", "success");
        return result;
    }
}
