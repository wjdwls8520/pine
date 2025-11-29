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
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;

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
        Page<PostResDto> postResDto = cs.getPostPage(page);

        result.put("post", postResDto);
        return result; //   작업폴더/jsp파일이름
    }



    @GetMapping("/community/ccreate") //  url
    public String create(){

        return "community/cCreate"; //   작업폴더/jsp파일이름
    }

    @PostMapping("/community/cCreate")
    public String insertPost(@ModelAttribute PostReqDto reqDto) {
        System.out.println(reqDto);
        cs.insertPost(reqDto);
        return "redirect:/community";
    }

    @GetMapping("/community/cdetail/{id}")
    public String getDetail(@PathVariable("id") Long id ) {
        // 서비스로가서 디테일가져와
        return "community/cDetail";
    }



}
