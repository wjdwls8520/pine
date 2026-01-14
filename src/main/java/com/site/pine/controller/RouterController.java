package com.site.pine.controller;

import com.site.pine.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class RouterController {

    private final MainService mainService;

    @GetMapping("/")
    public String main(Model model) {

        model.addAttribute("postAll", mainService.getBestPost());
        model.addAttribute("groupAll", mainService.getBestGroup());
        model.addAttribute("memberAll", mainService.getRandomMemberWithPosts());
        model.addAttribute("shortsAll", mainService.getBestShorts());

        return "index";
    }

    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping("/errorLogin")
    public String errorLogin() { return "errorLogin"; }
}
