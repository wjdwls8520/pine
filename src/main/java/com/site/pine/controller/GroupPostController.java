package com.site.pine.controller;

import com.site.pine.dto.community.CommunityCreateReqDto;
import com.site.pine.dto.member.MemberDto;
import com.site.pine.service.GroupAuthorizationService;
import com.site.pine.service.GroupPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class GroupPostController {

    // 그룹 인가 로직 서비스
    private final GroupAuthorizationService groupAuthorizationService;

    private final GroupPostService groupPostService;

    @GetMapping("/group/{groupId}/post/create")
    public String groupPost(
            @AuthenticationPrincipal MemberDto memberDto,
            @PathVariable Long groupId,
            Model model
        ) {

        groupAuthorizationService.validateLoginMember(); // 로그인 여부 확인
        groupAuthorizationService.getGroupMemberOrThrow(groupId, memberDto.getId()); // 그룹멤버인지 확인

        model.addAttribute("groupId", groupId);
        return "group/gPostCreate";
    }

    @PostMapping("/group/{groupId}/post/create")
    public String insertPost(
            @AuthenticationPrincipal MemberDto memberDto,
            @PathVariable Long groupId,
            @ModelAttribute CommunityCreateReqDto reqDto) {

        System.out.println(reqDto);

        groupAuthorizationService.validateLoginMember(); // 로그인 여부 확인
        groupAuthorizationService.getGroupMemberOrThrow(groupId, memberDto.getId()); // 그룹멤버인지 확인

        List<MultipartFile> fileList = reqDto.getFiles();
        for(MultipartFile file : fileList) {
            if(file.getSize() > 1000000) {
                throw new IllegalArgumentException("파일 용량이 1MB를 초과했습니다: " + file.getOriginalFilename());
            }
        }

        System.out.println(reqDto);
        groupPostService.insertPost(memberDto, groupId, reqDto);
        return "redirect:/group/" + groupId + "/post/main";
    }
}
