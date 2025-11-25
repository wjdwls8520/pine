package com.site.pine.controller;

import com.site.pine.dto.community.PostReqDto;
import com.site.pine.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService cs;

    @GetMapping("/community") //  url
    public String community(Model model){
        model.addAttribute("postList", cs.getAllPost());

        return "community/commu_main"; //   작업폴더/jsp파일이름
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
