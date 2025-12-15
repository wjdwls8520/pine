package com.site.pine.controller;

import com.site.pine.dto.group.GroupCategoryDto;
import com.site.pine.dto.group.GroupContentReqDto;
import com.site.pine.dto.member.MemberDto;
import com.site.pine.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class GroupController {

    private final GroupService gs;

    // 그룹페이지화면
    @GetMapping("/group")
    public String groups(@AuthenticationPrincipal MemberDto mdto) {

        System.out.println("@@@@@@@@" + mdto);
        return "group/group";
    }
    @GetMapping("/group/{page}")
    @ResponseBody
    public HashMap<String, Object> getAllGroups(@PathVariable("page") Integer page) {
        HashMap<String, Object> result = new HashMap<>();

        // 초기 페이지는 첫 페이지(0페이지)만 가져오기
        HashMap<String, Object> gorupsResDto = gs.getAllGroups(page);

        result.put("resDto", gorupsResDto);
        return result;
    }



    //그룹 생성
    @GetMapping("/group/gcreate")
    public String create(Model model){
        List<GroupCategoryDto> list = gs.getCategory();
        model.addAttribute("list", list);

        return "group/gCreate";
    }
    @PostMapping("/group/gcreate")
    public String create(@AuthenticationPrincipal MemberDto memberdto, @ModelAttribute GroupContentReqDto groupContentReqDto, RedirectAttributes redirectAttrs) {

        if (memberdto == null) {
            return "redirect:/errorLogin";
        }

        if(groupContentReqDto.getGroupImg() == null || groupContentReqDto.getGroupImg().isEmpty()){
            redirectAttrs.addFlashAttribute("msg", "[error] 파일이 비어있어 그룹생성을 하지 못했습니다.");
            return "redirect:/group";
        }

        if(groupContentReqDto.getGroupImg().getSize() > (5 * 1024 * 1024)) {
            redirectAttrs.addFlashAttribute("msg", "[error] 이미지 파일 크기는 5MB 이하여야 합니다.");
            return "redirect:/group";
        }

        try {
            gs.insertGroupContent(memberdto, groupContentReqDto);
            redirectAttrs.addFlashAttribute("msg", "그룹이 정상적으로 생성되었습니다.");
        } catch (IllegalStateException e) {
            redirectAttrs.addFlashAttribute("msg", e.getMessage());
        }

        return "redirect:/group";
    }

    // 그룹 디테일
    @GetMapping("/group/gdetail/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {

        try {
            model.addAttribute("groupDetail", gs.getGroup(id));
            System.out.println(gs.getGroup(id));
            return "group/gDetail";

        } catch (IllegalAccessException e) {
            model.addAttribute("msg", e.getMessage());
            return "errorPage"; // errorPage.jsp 로 이동
        }
    }

    // 그룹 업데이트
    @GetMapping("/group/gupdate")
    public String update(){

        return "group/gUpdate";
    }
}
