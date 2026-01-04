package com.site.pine.controller;

import com.site.pine.dto.community.CommunityCreateReqDto;
import com.site.pine.dto.community.CommunityDetailResDto;
import com.site.pine.dto.community.PostDetailDto;
import com.site.pine.dto.community.PostModifyDto;
import com.site.pine.dto.member.MemberDto;
import com.site.pine.service.CommunityService;
import com.site.pine.service.LikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService cs;


    @GetMapping("/community") //  url
    public String community(){
        return "community/commu_main"; //   작업폴더/jsp파일이름
    }

    @GetMapping("/community/{page}")
    @ResponseBody
    public HashMap<String, Object> getPostList(@AuthenticationPrincipal MemberDto mdto,@PathVariable("page") Integer page) {
        return cs.getPostPage(mdto,page);
    }

    @GetMapping("/community/ccreate") //  url
    public String create(){

        return "community/cCreate"; //   작업폴더/jsp파일이름
    }

    @PostMapping("/community/cCreate")
    public String insertPost(@AuthenticationPrincipal MemberDto mdto, @ModelAttribute CommunityCreateReqDto reqDto) {
        if (mdto == null) {
            // 로그인 안되어있으면 글쓰기 막고 로그인 페이지로 이동
            return "redirect:/login";
        }

        List<MultipartFile> fileList = reqDto.getFiles();
        for(MultipartFile file : fileList) {
            if(file.getSize() > 1000000) {
                throw new IllegalArgumentException("파일 용량이 1MB를 초과했습니다: " + file.getOriginalFilename());
            }
        }

        System.out.println(reqDto);
        cs.insertPost(mdto, reqDto);
        return "redirect:/community";
    }

    @GetMapping("/community/cdetail/{id}")
    public String getDetail(@AuthenticationPrincipal MemberDto mdto, @PathVariable("id") Long id, Model model) {
        Long memberId = (mdto != null) ? mdto.getId() : null; // 로그인 안하면 null
        CommunityDetailResDto post = cs.getDetail(memberId, id); // 서비스에서 memberId가 null인 경우 좋아요 체크를 생략하도록
        model.addAttribute("post", post);
        return "community/cDetail"; // JSP에서 ${post.필드} 로 접근
    }


    @DeleteMapping("/community/{id}")
    public ResponseEntity<String> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal MemberDto mdto // 현재 로그인한 사람
    ) {
        if (mdto == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        try {
            // 서비스 호출 (게시글번호, 내 회원번호)
            cs.deletePost(id, mdto.getId());
            return ResponseEntity.ok("삭제되었습니다."); // 200 OK

        } catch (IllegalArgumentException e) {
            // 권한이 없거나 글이 없는 경우
            return ResponseEntity.status(403).body(e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("삭제 중 오류가 발생했습니다.");
        }
    }

    // 1. 수정 페이지로 이동 (기존 데이터 들고 감)
    @GetMapping("/community/edit/{id}")
    public String editPage(@PathVariable Long id, Model model) {
        // 서비스에서 기존 게시글 정보 가져오기
        PostDetailDto postDto = cs.getPostDetail(id);

        // 모델에 담아서 write.html (또는 edit.html)로 보냄
        model.addAttribute("post", postDto);
        model.addAttribute("isEdit", true); // 프론트에서 수정모드인지 구분하려고

        return "community/cCreate";
    }

    // 2. 실제 수정 처리 (AJAX 요청)
    @PostMapping("/community/{id}")
    @ResponseBody
    public ResponseEntity<String> modifyPost(
            @PathVariable Long id,
            @ModelAttribute PostModifyDto dto,
            @AuthenticationPrincipal MemberDto mdto // 현재 로그인한 사용자
    ) {
        if (mdto == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        try {
            // 서비스 호출 (게시글 번호, 수정 데이터, 작성자 ID)
            cs.modifyPost(id, dto, mdto.getId());
            return ResponseEntity.ok("수정 성공");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).body(e.getMessage()); // 권한 없음 등
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("수정 중 오류 발생");
        }
    }

}
