package com.site.pine.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CommunityController {

    @GetMapping("/community") //  url
    public String community(){

        return "community/commu_main"; //   작업폴더/jsp파일이름
    }

    @GetMapping("/community/ccreate") //  url
    public String create(){

        return "community/cCreate"; //   작업폴더/jsp파일이름
    }
}
