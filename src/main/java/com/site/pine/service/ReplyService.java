package com.site.pine.service;

import com.site.pine.dto.member.MemberDto;
import com.site.pine.dto.reply.ReplyCreateReqDto;
import com.site.pine.dto.reply.ReplyResDto;
import com.site.pine.dto.reply.ReplyUpdateReqDto;
import com.site.pine.entity.Member;
import com.site.pine.entity.Reply;
import com.site.pine.entity.post.Post;
import com.site.pine.repository.MemberRepository;
import com.site.pine.repository.PostRepository;
import com.site.pine.repository.ReplyRepository;
import com.site.pine.repository.like.ReplyLikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReplyService {

    private final MemberRepository mr;
    private final ReplyRepository rr;
    private final PostRepository pr;
    private final ReplyLikeRepository rlr;

    @Transactional(readOnly = true)
    public Page<ReplyResDto> getReplyList(Long postId, Pageable pageable, Long memberId) {
        // 1. 게시글 존재 여부 확인
        Post post = pr.findById(postId).orElseThrow(() -> new IllegalStateException("존재하지 않는 게시글 입니다."));

        // 2. 부모 댓글만 페이징으로 가져옴
        // 자식 댓글들은 @BatchSize 설정 덕분에 DTO 변환 시점에 자동으로 효율적으로 가져와짐
        Page<Reply> parentReplies = rr.findParentReplies(postId, pageable);

        // 로그인한 멤버 엔티티 (좋아요 체크용)
        Member currentMember = (memberId != null)
                ? mr.findById(memberId).orElse(null)
                : null;

        // from(entity, false) -> 자식 데이터는 쿼리하지 않고, childCount만 가져감
        return parentReplies.map(reply -> {
            boolean isLiked = false;
            if (currentMember != null) {
                // DB에서 좋아요 여부 확인
                isLiked = rlr.findByReplyAndMember(reply, currentMember).isPresent();
            }
            return ReplyResDto.from(reply, false, isLiked);
        });
    }

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

        return ReplyResDto.from(reply, false, false);

    }

    // 대댓글 더보기 클릭 시 호출될 메서드
    @Transactional(readOnly = true)
    public List<ReplyResDto> getChildReplies(Long parentId, Long memberId) {
        // 부모가 존재하는지 확인
        Reply parent = rr.findById(parentId).orElseThrow(() -> new IllegalStateException("부모 댓글이 존재하지 않습니다."));

        // 자식들 조회
        List<Reply> children = rr.findChildReplies(parentId);

        Member currentMember = (memberId != null)
                ? mr.findById(memberId).orElse(null)
                : null;

        // 자식들을 DTO로 변환
        return children.stream()
                .map(reply -> {
                    boolean isLiked = false;
                    if (currentMember != null) {
                        isLiked = rlr.findByReplyAndMember(reply, currentMember).isPresent();
                    }
                    return ReplyResDto.from(reply, false, isLiked);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateComment(Long id, ReplyUpdateReqDto reqDto) {
        Reply reply = rr.findById(reqDto.getReplyId()).orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        if("Y".equals(reply.getDeleteYN())) {
            throw new IllegalArgumentException("삭제된 댓글은 수정할 수 없습니다.");
        }

        if(!reply.getMember().getId().equals(id)) {
            throw new IllegalStateException("자신의 댓글만 수정할 수 있습니다.");
        }

        reply.updateComment(reqDto.getContent());
    }

    @Transactional
    public void deleteReply(Long replyId, Long memberId) {
        //  댓글 조회
        Reply reply = rr.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        // 이미 삭제된 댓글인지 확인 (중복 감소 방지)
        if ("Y".equals(reply.getDeleteYN())) {
            throw new IllegalArgumentException("이미 삭제된 댓글입니다.");
        }

        // 권한 체크 (작성자 본인 확인)
        if (!reply.getMember().getId().equals(memberId)) {
            throw new IllegalStateException("본인의 댓글만 삭제할 수 있습니다.");
        }
        // soft delete
        reply.changeDeleteYn("Y");

        // 게시글의 댓글 수 감소
        pr.decreaseReplyCount(reply.getPost().getId());
    }


}
