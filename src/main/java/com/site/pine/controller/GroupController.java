package com.site.pine.controller;

import com.site.pine.dto.group.GroupCategoryDto;
import com.site.pine.dto.group.GroupContentReqDto;
import com.site.pine.dto.group.GroupContentResDto;
import com.site.pine.dto.group.GroupMemberResDto;
import com.site.pine.dto.member.MemberDto;
import com.site.pine.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
    public String groups(@AuthenticationPrincipal MemberDto mdto, Model model) {
        model.addAttribute("loginUser", mdto);
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
    public String create(@AuthenticationPrincipal MemberDto memberdto, Model model){
        if(memberdto == null) return "redirect:/errorLogin";

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
    @GetMapping("/group/gdetail/{groupId}")
    public String detail(@AuthenticationPrincipal MemberDto memberdto, @PathVariable("groupId") Long groupId, Model model, RedirectAttributes redirectAttrs) {

        try {
            GroupContentResDto getGroupDetail = gs.getGroup(groupId);
            GroupMemberResDto isGroupMember = gs.getGroupMemberInfo(memberdto, groupId);

            model.addAttribute("groupDetail", getGroupDetail);
            model.addAttribute("isGroupMember", isGroupMember);
            return "group/gDetail";

        } catch (IllegalStateException e) {
            redirectAttrs.addFlashAttribute("msg", e.getMessage());
            return "redirect:/group";
        }
    }

    // 그룹 업데이트
    @GetMapping("/group/gupdate/{groupId}")
    public String update(@PathVariable("groupId") Long groupId, @AuthenticationPrincipal MemberDto memberdto, Model model, RedirectAttributes redirectAttrs) {

        if(memberdto == null) return "redirect:/errorLogin";

        try {
            List<GroupCategoryDto> list = gs.getCategory();
            model.addAttribute("list", list);

            GroupContentResDto getGroupDetail = gs.getGroup(groupId);
            model.addAttribute("groupDetail", getGroupDetail);

            return "group/gUpdate";

        } catch (IllegalStateException e) {
            redirectAttrs.addFlashAttribute("msg", e.getMessage());
            return "redirect:/group";
        }
    }
    @PostMapping("/group/gupdate/{groupId}")
    public String update(@AuthenticationPrincipal MemberDto memberdto, @PathVariable("groupId") Long groupId, @ModelAttribute GroupContentReqDto groupContentReqDto, RedirectAttributes redirectAttrs) {

        if (memberdto == null) {
            return "redirect:/errorLogin";
        }

        if(groupContentReqDto.getGroupImg().getSize() > (5 * 1024 * 1024)) {
            redirectAttrs.addFlashAttribute("msg", "[error] 이미지 파일 크기는 5MB 이하여야 합니다.");
            return "redirect:/group/gdetail/" + groupId;
        }

        if(groupContentReqDto.getCategoryIds() == null || groupContentReqDto.getCategoryIds().isEmpty()){
            redirectAttrs.addFlashAttribute("msg", "[error] 카테고리는 최소 1개 이상 선택하셔야 합니다.");
            return "redirect:/group/gdetail/" + groupId;
        }

        try {
            gs.updateGroupContent(memberdto, groupId, groupContentReqDto);
            redirectAttrs.addFlashAttribute("msg", "수정이 완료되었습니다.");
        } catch (IllegalStateException e) {
            redirectAttrs.addFlashAttribute("msg", e.getMessage());
        }

        return "redirect:/group/gdetail/" + groupId;
    }

    @PostMapping("/group/gdelete/{groupId}")
    @ResponseBody
    public ResponseEntity<HashMap<String, Object>> groupDelete(
        @PathVariable Long groupId,
        @AuthenticationPrincipal MemberDto memberdto
    ) {

        HashMap<String, Object> result = new HashMap<>();

        if (memberdto == null) {
            result.put("msg", "redirect:/errorLogin");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }

        try {
            gs.deleteGroup(groupId, memberdto);
        } catch (AccessDeniedException e) {
            result.put("msg", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
        } catch (IllegalStateException e) {
            result.put("msg", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }

        result.put("msg", "success");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/group/gdetail/{groupId}/gjoinlist")
    public String getJoinList(@PathVariable("groupId") Long groupId, @AuthenticationPrincipal MemberDto memberdto, Model model, RedirectAttributes redirectAttrs) {
        if (memberdto == null) {
            return "redirect:/errorLogin";
        }

        return "group/gJoinList";
    }

}
