package com.site.pine.controller;

import com.site.pine.dto.group.GroupCategoryDto;
import com.site.pine.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GroupController {

    private final GroupService gs;

    @GetMapping("/group")
    public String groups(){

        return "group/group";
    }

    @GetMapping("/group/gcreate")
    public String create(Model model){
        List<GroupCategoryDto> list = gs.getCategory();
        model.addAttribute("list", list);

        return "group/gCreate";
    }

    @GetMapping("/group/gupdate")
    public String update(){

        return "group/gUpdate";
    }
}
