package com.site.pine.controller;

import com.site.pine.dto.member.MemberDto;
import com.site.pine.dto.reply.ReplyCreateReqDto;
import com.site.pine.dto.reply.ReplyResDto;
import com.site.pine.dto.reply.ReplyUpdateReqDto;
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

    //조회
    @ResponseBody
    @GetMapping("/reply/list")
    public Page<ReplyResDto> list(
            @RequestParam Long postId,
            @PageableDefault(size = 10, sort = "writeDate", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal MemberDto mdto
    ) {
        Long memberId = (mdto != null) ? mdto.getId() : null;
        return rs.getReplyList(postId, pageable, memberId);
    }

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


    // 대댓글(자식) 목록 조회 API
    // 프론트에서 "답글 보기" 버튼 클릭 시 -> /reply/10/children 호출
    @ResponseBody
    @GetMapping("/reply/{parentId}/children")
    public List<ReplyResDto> getChildren(
            @PathVariable Long parentId,
            @AuthenticationPrincipal MemberDto mdto
    ) {
        Long memberId = (mdto != null) ? mdto.getId() : null;
        return rs.getChildReplies(parentId, memberId);
    }

    @ResponseBody
    @PutMapping("/reply")
    public ResponseEntity<String> update(
            @AuthenticationPrincipal MemberDto mdto,
            @RequestBody ReplyUpdateReqDto reqDto
    ){
        if (mdto == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요한 서비스입니다.");
        }

        try {
            rs.updateComment(mdto.getId(), reqDto);
            return ResponseEntity.ok("댓글이 수정되었습니다.");
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ResponseBody
    @DeleteMapping("/reply/{replyId}")
    public ResponseEntity<String> delete(
            @PathVariable Long replyId,
            @AuthenticationPrincipal MemberDto mdto
    ){
        if(mdto == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try{
            rs.deleteReply(replyId, mdto.getId());
            return ResponseEntity.ok("댓글이 성공적으로 삭제되었습니다.");
        } catch (IllegalStateException e) {
            // 남의 거 삭제하려 함 "권한 없음" 403
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());

        } catch (IllegalArgumentException e) {
            // 이미 삭제됐거나 없는 댓글 "잘못된 요청" 400
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e){
            // 알 수 없는 서버 에러 500
            log.error("댓글삭제 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }


}
