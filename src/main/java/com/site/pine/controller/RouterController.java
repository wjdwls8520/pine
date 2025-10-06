package com.site.pine.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RouterController {

//    @Autowired

    @GetMapping("/")
    public String main() {
        return "index";
    }


}
