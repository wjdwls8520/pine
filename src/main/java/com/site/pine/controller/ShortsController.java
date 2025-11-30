package com.site.pine.controller;

import com.site.pine.dto.shorts.ShortsResDto;
import com.site.pine.dto.shorts.ShortsUploadReqDto;
import com.site.pine.service.shortsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ShortsController {

    private final shortsService ss;

    @GetMapping("/shorts")
    public String shorts(){

        return "shorts/shorts";
    }

    @GetMapping("/shorts/{page}")
    @ResponseBody
    public List<ShortsResDto> shorts(@PathVariable("page") int page){
        return ss.getAllShorts(page);
    }

    @GetMapping("/shorts/shortsUpload")
    public String shortsCreate(){

        return "shorts/shortsUpload";
    }

    @PostMapping("/shorts/shortsUpload")
    public String shortsUpload(@ModelAttribute ShortsUploadReqDto shortsuploadreqdto) throws IOException {
        System.out.println(shortsuploadreqdto);
        ss.insertShorts(shortsuploadreqdto);
        return "redirect:/shorts";
    }




}
