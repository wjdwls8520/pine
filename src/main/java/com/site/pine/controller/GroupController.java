package com.site.pine.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GroupController {

    @GetMapping("/group")
    public String groups(){

        return "group/group";
    }

    @GetMapping("/group/gcreate")
    public String create(){

        return "group/gCreate";
    }

    @GetMapping("/group/gupdate")
    public String update(){

        return "group/gUpdate";
    }
}
