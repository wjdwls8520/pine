package com.site.pine.controller;

import com.site.pine.dto.group.GroupCategoryDto;
import com.site.pine.dto.group.GroupContentReqDto;
import com.site.pine.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class GroupController {

    private final GroupService gs;

    // 그룹페이지화면
    @GetMapping("/group")
    public String groups(){

        return "group/group";
    }

    //그룹 생성
    @GetMapping("/group/gcreate")
    public String create(Model model){
        List<GroupCategoryDto> list = gs.getCategory();
        model.addAttribute("list", list);

        return "group/gCreate";
    }
    @PostMapping("/group/gcreate")
    public String create(@ModelAttribute GroupContentReqDto groupContentReqDto, RedirectAttributes redirectAttrs) throws IOException {
        System.out.println(groupContentReqDto);

        if(groupContentReqDto.getGroupImg() == null || groupContentReqDto.getGroupImg().isEmpty()){
            redirectAttrs.addFlashAttribute("msg", "[error] 파일이 비어있어 그룹생성을 하지 못했습니다.");
            return "redirect:/group";
        }
        gs.insertGroupContent(groupContentReqDto);
        redirectAttrs.addFlashAttribute("msg", "그룹이 정상적으로 생성되었습니다.");
        return "redirect:/group";
    }


    // 그룹 업데이트
    @GetMapping("/group/gupdate")
    public String update(){

        return "group/gUpdate";
    }
}
