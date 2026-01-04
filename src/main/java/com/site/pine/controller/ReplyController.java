package com.site.pine.controller;

import com.site.pine.dto.member.MemberDto;
import com.site.pine.dto.reply.ReplyCreateReqDto;
import com.site.pine.dto.reply.ReplyResDto;
import com.site.pine.service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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
        if(mdto == null){
            throw new IllegalStateException("로그인이 필요한 서비스입니다");
        }
        return rs.createReply(mdto, reqDto);
    }

    //조회
    @ResponseBody
    @GetMapping("/reply/list")
    public Page<ReplyResDto> list(
            @RequestParam Long postId,
            @PageableDefault(size = 10, sort = "writeDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return rs.getReplyList(postId, pageable);
    }

    // 삭제
    @ResponseBody // AJAX 요청이니까 필수
    @DeleteMapping("/reply/{replyId}")
    public ResponseEntity<String> delete(
            @PathVariable Long replyId,
            @AuthenticationPrincipal MemberDto mdto
    ){
        // 1. 로그인 안 함 -> 401 에러 코드 전송
        if(mdto == null){
            // "redirect:/login" (X) -> 프론트가 알아먹게 상태 코드(401)를 줌
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try{
            rs.deleteReply(replyId, mdto.getId());
            // 2. 성공 -> 200 OK
            return ResponseEntity.ok("댓글이 성공적으로 삭제되었습니다.");
        } catch (Exception e){
            log.error("댓글삭제 실패", e);
            // 3. 실패 -> 400 Bad Request
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


}
