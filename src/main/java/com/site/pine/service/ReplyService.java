package com.site.pine.service;

import com.site.pine.dto.member.MemberDto;
import com.site.pine.dto.reply.ReplyCreateReqDto;
import com.site.pine.dto.reply.ReplyResDto;
import com.site.pine.entity.Member;
import com.site.pine.entity.Reply;
import com.site.pine.entity.post.Post;
import com.site.pine.repository.MemberRepository;
import com.site.pine.repository.PostRepository;
import com.site.pine.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReplyService {

    private final MemberRepository mr;
    private final ReplyRepository rr;
    private final PostRepository pr;

    @Transactional
    public ReplyResDto createReply(MemberDto mdto, ReplyCreateReqDto reqDto) {

        // 회원 조회
        Member member = mr.findById(mdto.getId()).orElseThrow(() -> new IllegalStateException("존재하지 않는 회원입니다."));

        // 게시글 조회
        Post post = pr.findById(reqDto.getPostId()).orElseThrow(()-> new IllegalStateException("존재하지 않는 게시글입니다. id=" + reqDto.getPostId()));

        // 댓글 엔티티 생성
        Reply reply = new Reply();
        reply.setContent(reqDto.getContent());
        reply.setDeleteYN("N");
        reply.setPost(post);
        reply.setMember(member);

        // 대댓글 처리
        if (reqDto.getParentId() != null) {
            Reply parent = rr.findById(reqDto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 존재하지 않습니다."));
            // 부모 댓글과 자식 댓글이 같은 게시글인지 확인 (데이터 꼬임 방지)
            if (!parent.getPost().getId().equals(post.getId())) {
                throw new IllegalArgumentException("부모 댓글과 다른 게시글에 대댓글을 달 수 없습니다.");
            }
            if (parent.getParent() != null) {
                throw new IllegalArgumentException("대댓글에는 답글을 작성할 수 없습니다.");
            }
            reply.setParent(parent);
        }

        rr.save(reply);

        pr.increaseReplyCount(post.getId()); // DB에 댓글 수 증가 쿼리 실행
        // 현재 메모리에 있는 post 객체의 카운트도 1 올려줌 (DTO 변환용)
        post.setReplyCount(post.getReplyCount() + 1);

        return ReplyResDto.from(reply);

    }

    @Transactional(readOnly = true)
    public Page<ReplyResDto> getReplyList(Long postId, Pageable pageable) {
        // 1. 게시글 존재 여부 확인
         Post post = pr.findById(postId).orElseThrow(() -> new IllegalStateException("존재하지 않는 게시글 입니다."));

        // 2. 부모 댓글만 페이징으로 가져옴
        // 자식 댓글들은 @BatchSize 설정 덕분에 DTO 변환 시점에 자동으로 효율적으로 가져와짐
        Page<Reply> parentReplies = rr.findParentReplies(postId, pageable);

        // 3. 엔티티 -> DTO 변환 (from 메서드 내부에서 자식 변환 및 삭제 마스킹 처리됨)
        return parentReplies.map(ReplyResDto::from);
    }

    @Transactional
    public void deleteReply(Long replyId, Long memberId) {
        // 1. 댓글 조회
        Reply reply = rr.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        // 2. 권한 체크 (작성자 본인 확인)
        if (!reply.getMember().getId().equals(memberId)) {
            throw new IllegalStateException("본인의 댓글만 삭제할 수 있습니다.");
        }

        // 3. 삭제 로직 분기
        if (reply.getChildren().isEmpty()) {
            // 자식이 없으면 -> DB에서 진짜 삭제 (Hard Delete)
            rr.delete(reply);
            pr.decreaseReplyCount(reply.getPost().getId());
        } else {
            // 자식이 있으면 -> "삭제된 댓글입니다" 상태로 변경 (Soft Delete) -> 대댓글 구조 유지
            reply.setDeleteYN("Y");
        }
    }
}
