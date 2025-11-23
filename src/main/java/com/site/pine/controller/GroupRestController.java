package com.site.pine.controller;

import com.site.pine.dto.group.GroupCategoryDto;
import com.site.pine.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//@RestController
//@RequestMapping("/api/group")
//@RequiredArgsConstructor
//public class GroupRestController {
//
//    private final GroupService gs;
//
//    @GetMapping("/gCategory")
//    public Model gCategory(Model model) {
//        List<GroupCategoryDto> list = gs.getCategory();
//        model.addAttribute("list", list);
//        return model;
//    }
//}
