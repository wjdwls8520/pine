package com.site.pine.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.Map;

@Controller
public class NewsController {

    @GetMapping("/newsdetail/{id}")
    public String newsDetail(@PathVariable("id") Integer id, Model model) {

        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        model.addAttribute("paramId", map);

        return "news/newsDetail";
    }
}
