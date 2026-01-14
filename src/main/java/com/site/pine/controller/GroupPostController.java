package com.site.pine.controller;

import com.site.pine.dto.community.CommunityCreateReqDto;
import com.site.pine.dto.community.CommunityDetailResDto;
import com.site.pine.dto.community.PostDetailDto;
import com.site.pine.dto.community.PostModifyDto;
import com.site.pine.dto.group.GroupPostDetailDto;
import com.site.pine.dto.group.GroupPostDetailResDto;
import com.site.pine.dto.member.MemberDto;
import com.site.pine.service.GroupAuthorizationService;
import com.site.pine.service.GroupPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class GroupPostController {

    // 그룹 인가 로직 서비스
    private final GroupAuthorizationService groupAuthorizationService;

    private final GroupPostService groupPostService;

    @GetMapping("/group/{groupId}/post/main")
    public String groupPostMain(@PathVariable("groupId") Long groupId, Model model) {
        String groupName = groupAuthorizationService.getGroupOrThrow(groupId).getGroupName();
        model.addAttribute("groupName", groupName);

        model.addAttribute("groupId", groupId);
        return "group/gPostMain";
    }

    @GetMapping("/group/{groupId}/post/main/{page}")
    @ResponseBody
    public HashMap<String, Object> getPostList(
            @AuthenticationPrincipal MemberDto memberDto,
            @PathVariable("groupId") Long groupId,
            @PathVariable("page") Integer page
    ) {
        groupAuthorizationService.validateLoginMember(); // 로그인 여부 확인
        groupAuthorizationService.getGroupMemberOrThrow(groupId, memberDto.getId()); // 그룹멤버인지 확인
        return groupPostService.getPostPage(memberDto,page, groupId);
    }

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

    @GetMapping("/group/{groupId}/post/detail/{id}")
    public String getDetail(
            @AuthenticationPrincipal MemberDto memberDto,
            @PathVariable Long groupId,
            @PathVariable("id") Long id, Model model
    ) {
        groupAuthorizationService.validateLoginMember(); // 로그인 여부 확인
        groupAuthorizationService.getGroupMemberOrThrow(groupId, memberDto.getId()); // 그룹멤버인지 확인

        Long memberId = memberDto.getId();
        GroupPostDetailResDto post = groupPostService.getDetail(memberId, id); // 서비스에서 memberId가 null인 경우 좋아요 체크를 생략하도록
        model.addAttribute("post", post);
        model.addAttribute("groupId", groupId);
        if (memberId != null) {
            model.addAttribute("loginUserId", memberId);
        }
        return "group/gPostDetail"; // JSP에서 ${post.필드} 로 접근
    }

    // 1. 수정 페이지로 이동 (기존 데이터 들고 감)
    @GetMapping("/group/{groupId}/post/{id}/edit")
    public String editPage(@AuthenticationPrincipal MemberDto memberDto, @PathVariable Long groupId, @PathVariable Long id, Model model) {
        groupAuthorizationService.validateLoginMember(); // 로그인 여부 확인
        groupAuthorizationService.getGroupMemberOrThrow(groupId, memberDto.getId()); // 그룹멤버인지 확인

        // 서비스에서 기존 게시글 정보 가져오기
        GroupPostDetailDto postDto = groupPostService.getPostDetail(id);

        // 모델에 담아서 write.html (또는 edit.html)로 보냄
        model.addAttribute("post", postDto);

        model.addAttribute("isEdit", true); // 프론트에서 수정모드인지 구분하려고

        model.addAttribute("groupId", groupId);
        return "group/gPostCreate";
    }

    // 2. 실제 수정 처리 (AJAX 요청)
    @PostMapping("/group/{groupId}/post/{id}/edit")
    @ResponseBody
    public ResponseEntity<String> modifyPost(
            @PathVariable Long groupId,
            @PathVariable Long id,
            @ModelAttribute PostModifyDto dto,
            @AuthenticationPrincipal MemberDto memberDto // 현재 로그인한 사용자
    ) {
        if (memberDto == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        groupAuthorizationService.validateLoginMember(); // 로그인 여부 확인
        groupAuthorizationService.getGroupMemberOrThrow(groupId, memberDto.getId()); // 그룹멤버인지 확인

        try {
            // 서비스 호출 (게시글 번호, 수정 데이터, 작성자 ID)
            groupPostService.modifyPost(id, dto, memberDto.getId());
            return ResponseEntity.ok("수정 성공");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).body(e.getMessage()); // 권한 없음 등
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("수정 중 오류 발생");
        }
    }

    @DeleteMapping("/group/{groupId}/post/{id}/delete")
    public ResponseEntity<String> deletePost(
            @PathVariable Long groupId,
            @PathVariable Long id,
            @AuthenticationPrincipal MemberDto memberDto // 현재 로그인한 사람
    ) {
        if (memberDto == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        groupAuthorizationService.validateLoginMember(); // 로그인 여부 확인
        groupAuthorizationService.getGroupMemberOrThrow(groupId, memberDto.getId()); // 그룹멤버인지 확인

        try {
            // 서비스 호출 (게시글번호, 내 회원번호)
            groupPostService.deletePost(id, memberDto.getId());
            return ResponseEntity.ok("삭제되었습니다."); // 200 OK

        } catch (IllegalArgumentException e) {
            // 권한이 없거나 글이 없는 경우
            return ResponseEntity.status(403).body(e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("삭제 중 오류가 발생했습니다.");
        }
    }
}
