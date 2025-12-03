package com.site.pine.controller;

import com.site.pine.dto.community.PostReqDto;
import com.site.pine.dto.community.PostResDto;
import com.site.pine.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
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
    public HashMap<String, Object> getPostList(@PathVariable("page") Integer page) {
        HashMap<String, Object> result = new HashMap<>();

        // 초기 페이지는 첫 페이지(0페이지)만 가져오기
        HashMap<String, Object> postResDto = cs.getPostPage(page);

        result.put("post", postResDto);
        return result; //   작업폴더/jsp파일이름
    }

    @GetMapping("/community/ccreate") //  url
    public String create(){

        return "community/cCreate"; //   작업폴더/jsp파일이름
    }

    @PostMapping("/community/cCreate")
    public String insertPost(@ModelAttribute PostReqDto reqDto) throws IOException {
        List<MultipartFile> fileList = reqDto.getFiles();
        for(MultipartFile file : fileList) {
            if(file.getSize() > 1000000) {
                throw new IllegalArgumentException("파일 용량이 1MB를 초과했습니다: " + file.getOriginalFilename());
            }
        }

        System.out.println(reqDto);
        cs.insertPost(reqDto);
        return "redirect:/community";
    }

    @GetMapping("/community/cdetail/{id}")
    public String getDetail(@PathVariable("id") Long id, Model model) {
        PostResDto post = cs.getDetail(id);
        model.addAttribute("post", post);
        return "community/cDetail"; // JSP에서 ${post.필드} 로 접근
    }

    @PostMapping("/community/likeCount/{postId}")
    @ResponseBody  // JSON으로 반환
    public HashMap<String, Object> likeCount(@PathVariable("postId") Long postId,
                                             @RequestBody HashMap<String, Object> body) {

        // body에서 like 상태 가져오기
        boolean like = (Boolean) body.get("like");

        // 서비스에서 like 수 업데이트
        int updatedLikeCount = cs.updateLikeCount(postId, like);

        // 결과 JSON으로 반환
        HashMap<String, Object> result = new HashMap<>();
        result.put("likeCount", updatedLikeCount);
        return result;
    }

}
