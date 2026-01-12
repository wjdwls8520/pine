package com.site.pine.controller;

import com.site.pine.dto.member.MemberDto;
import com.site.pine.dto.shorts.ShortsResDto;
import com.site.pine.dto.shorts.ShortsUpdateReqDto;
import com.site.pine.dto.shorts.ShortsUploadReqDto;
import com.site.pine.service.ShortsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

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

    @ResponseBody
    @PostMapping("/shorts/shortsUpload")
    public Map<String, Object> shortsUpload(@AuthenticationPrincipal MemberDto memberdto, @ModelAttribute ShortsUploadReqDto shortsuploadreqdto){
        Map<String, Object> map = new HashMap<>();
        System.out.println(shortsuploadreqdto);
        System.out.println(memberdto);

        if (memberdto == null) {
            map.put("success", false);
            map.put("msg", "로그인 후 이용해주세요");
            return map;
        }

        MultipartFile video = shortsuploadreqdto.getVideoFile();
        MultipartFile thumbnail = shortsuploadreqdto.getThumbnailFile();

        // 1. 영상 검증
        if(video == null || video.isEmpty()) {
            map.put("success", false);
            map.put("msg", "영상 파일은 필수입니다.");
            return map;
        }

        if(video.getSize() > 10 * 1024 * 1024) {
            map.put("success", false);
            map.put("msg", "영상은 최대 10MB까지 업로드 가능합니다.");
            return map;
        }

        if(video.getContentType() == null || !video.getContentType().startsWith("video/")) {
            map.put("success", false);
            map.put("msg", "영상 파일만 업로드할 수 있습니다.");
            return map;
        }

        // 2. 수동 썸네일 검증
        if("manual".equals(shortsuploadreqdto.getThumbnailType())){
            if (thumbnail == null || thumbnail.isEmpty()) {
                map.put("success", false);
                map.put("msg", "썸네일 파일이 없습니다.");
                return map;
            }

            if(thumbnail.getSize() > 5 * 1024 * 1024) {
                map.put("success", false);
                map.put("msg", "썸네일 이미지는 5MB 이내로 가능합니다.");
                return map;
            }

            if(thumbnail.getContentType() == null || !thumbnail.getContentType().startsWith("image/")) {
                map.put("success", false);
                map.put("msg", "이미지 파일만 썸네일로 사용할 수 있습니다.");
                return map;
            }
        }

        try {
            ss.insertShorts(shortsuploadreqdto, memberdto);
            map.put("success", true);
            map.put("msg", "쇼츠가 정상적으로 업로드되었습니다");
        } catch (IllegalStateException e) {
            map.put("success", false);
            map.put("msg", e.getMessage());
        } catch (Exception e) {
            log.error("쇼츠 업로드 중 오류 발생", e);
            map.put("success", false);
            map.put("msg", "업로드 중 알 수 없는 오류가 발생했습니다.");
        }

        return map;
    }

    @GetMapping("/shorts/detail/{postId}")
    @ResponseBody
    public ResponseEntity<ShortsResDto> getShortDetail(@PathVariable Long postId) {
        ShortsResDto dto = ss.getShortsDetail(postId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/shorts/shortsUpdate/{postId}")
    public String editShortsPage(@PathVariable Long postId,
                                 @AuthenticationPrincipal MemberDto memberDto,
                                 Model model) {
        ShortsResDto shorts = ss.getShortsDetail(postId);

        if (memberDto == null || !shorts.getMemberId().equals(memberDto.getId())) {
            return "redirect:/errorLogin";
        }
        model.addAttribute("shorts", shorts);

        return "shorts/shortsUpdate";
    }

    @ResponseBody
    @PostMapping("/shorts/shortsUpdate")
    public Map<String, Object> updateShorts(
            @AuthenticationPrincipal MemberDto memberDto,
            @ModelAttribute ShortsUpdateReqDto dto
    ) {
        Map<String, Object> map = new HashMap<>();

        if (memberDto == null) {
            map.put("success", false);
            map.put("msg", "로그인이 필요한 서비스입니다.");
            return map;
        }

        try {
            ss.updateShorts(dto, memberDto.getId());
            map.put("success", true);
            map.put("msg", "수정이 완료되었습니다.");

        } catch (IllegalArgumentException e) {
            map.put("success", false);
            map.put("msg", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            map.put("success", false);
            map.put("msg", "수정 중 오류가 발생했습니다.");
        }
        return map;
    }




}
