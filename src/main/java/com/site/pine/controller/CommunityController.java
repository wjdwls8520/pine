package com.site.pine.controller;

import com.site.pine.dto.community.CommunityCreateReqDto;
import com.site.pine.dto.community.CommunityDetailResDto;
import com.site.pine.dto.member.MemberDto;
import com.site.pine.service.CommunityService;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/community/likeCount/{postId}")
    @ResponseBody  // JSON으로 반환
    public HashMap<String, Object> likeCount(@PathVariable("postId") Long postId,
                                             @AuthenticationPrincipal MemberDto mdto) {

        HashMap<String, Object> result = new HashMap<>();

        if (mdto == null) { //로그인체크
            result.put("success", false);
            result.put("message", "로그인이 필요합니다.");
            return result;
        }

        Long memberId = mdto.getId();
        int likeCount = cs.toggleLike(postId, memberId);

        result.put("success", true);
        result.put("likeCount", likeCount);
        return result;
    }

}
