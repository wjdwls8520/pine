package com.site.pine.controller;

import com.site.pine.dto.member.MemberDto;
import com.site.pine.dto.reply.ReplyCreateReqDto;
import com.site.pine.dto.reply.ReplyResDto;
import com.site.pine.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService rs;


    //입력
    @ResponseBody
    @PostMapping("/reply")
    public ReplyResDto create(
            @AuthenticationPrincipal MemberDto mdto,
            @RequestBody ReplyCreateReqDto reqDto
    ) {
        return rs.createReply(mdto, reqDto);
    }

    //조회
    @ResponseBody
    @GetMapping("/reply/list")
    public List<ReplyResDto> list(
            @RequestParam Long targetId
    ) {
        return rs.getReplyList(targetId);
    }
}
