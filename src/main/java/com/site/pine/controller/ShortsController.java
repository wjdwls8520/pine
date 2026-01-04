package com.site.pine.controller;

import com.site.pine.dto.member.MemberDto;
import com.site.pine.dto.shorts.ShortsUploadReqDto;
import com.site.pine.service.ShortsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ShortsController {

    private final ShortsService ss;

    @GetMapping({"/shorts", "/shorts/view/{postId}"})
    public String shorts(
            @PathVariable(required = false) Long postId, Model model){
        if (postId != null) {
            model.addAttribute("targetPostId", postId);
        }
        return "shorts/shorts";
    }

    @GetMapping("/shorts/{page}")
    @ResponseBody
    public HashMap<String, Object> shorts(@AuthenticationPrincipal MemberDto memberdto, @PathVariable("page") int page){
        return ss.getAllShorts(memberdto, page);
    }

    @GetMapping("/shorts/shortsUpload")
    public String shortsCreate(@AuthenticationPrincipal MemberDto memberdto, RedirectAttributes redirectAttributes){
        if(memberdto == null){
//            redirectAttributes.addFlashAttribute("msg", "로그인 후 이용가능합니다.");
            return "redirect:/errorLogin";
        }
        return "shorts/shortsUpload";
    }

    @PostMapping("/shorts/shortsUpload")
    public String shortsUpload(@AuthenticationPrincipal MemberDto memberdto, @ModelAttribute ShortsUploadReqDto shortsuploadreqdto, RedirectAttributes redirectAttrs){
        System.out.println(shortsuploadreqdto);
        System.out.println(memberdto);

        if (memberdto == null) {
            redirectAttrs.addFlashAttribute("msg", "로그인 후 이용해주세요");
            return "redirect:/login";
        }

        MultipartFile video = shortsuploadreqdto.getVideoFile();
        MultipartFile thumbnail = shortsuploadreqdto.getThumbnailFile();

        // 1. 영상 검증
        if(video == null || video.isEmpty()) {
            redirectAttrs.addFlashAttribute("msg","영상 파일은 필수입니다.");
            return "redirect:/shorts";
        }

        if(video.getSize() > 10 *  1024 * 1024) {
            redirectAttrs.addFlashAttribute("msg", "영상은 최대 10MB까지 업로드 가능합니다.");
            return "redirect:/shorts";
        }

        if(video.getContentType() == null || !video.getContentType().startsWith("video/")) {
            redirectAttrs.addFlashAttribute("msg", "영상 파일만 업로드할 수 있습니다.");
            return "redirect:/shorts";
        }

        // 2. 수동 썸네일 검증
        if("manual".equals(shortsuploadreqdto.getThumbnailType())){
            if (thumbnail == null || thumbnail.isEmpty()) {
                redirectAttrs.addFlashAttribute("msg","썸네일 파일이 없습니다.");
                return "redirect:/shorts";
            }

            if(thumbnail.getSize() > 5 *  1024 * 1024) {
                redirectAttrs.addFlashAttribute("msg","썸네일 이미지는 5MB 이내로 가능합니다.");
                return "redirect:/shorts";
            }

            if(thumbnail.getContentType() == null || !thumbnail.getContentType().startsWith("image/")) {
                redirectAttrs.addFlashAttribute("msg", "이미지 파일만 썸네일로 사용할 수 있습니다.");
                return "redirect:/shorts";
            }
        }

        try {
            ss.insertShorts(shortsuploadreqdto, memberdto);
            redirectAttrs.addFlashAttribute("msg", "쇼츠가 정상적으로 업로드되었습니다");
        }catch (IllegalStateException e){
            redirectAttrs.addFlashAttribute("msg", e.getMessage());
        }

        return "redirect:/shorts";
    }




}
