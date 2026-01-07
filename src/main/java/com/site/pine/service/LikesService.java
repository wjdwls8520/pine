package com.site.pine.service;

import com.site.pine.dto.like.LikeReqDto;
import com.site.pine.dto.like.LikeResDto;
import com.site.pine.entity.Member;
import com.site.pine.entity.Reply;
import com.site.pine.entity.like.PostLike;
import com.site.pine.entity.like.ReplyLike;
import com.site.pine.entity.post.Post;
import com.site.pine.repository.MemberRepository;
import com.site.pine.repository.PostRepository;
import com.site.pine.repository.ReplyRepository;
import com.site.pine.repository.like.PostLikeRepository;
import com.site.pine.repository.like.ReplyLikeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class LikesService {

    private final PostLikeRepository postLikeRepository;
    private final ReplyLikeRepository replyLikeRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final ReplyRepository replyRepository;

    public LikeResDto toggleLike(LikeReqDto reqDto, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보 없음"));

        boolean isLiked;
        Integer currentCount; // 최종적으로 반환할 DB 값

        // 1. 게시글(POST)
        if ("POST".equalsIgnoreCase(reqDto.getTargetType())) {

            // 검증용 조회 (존재 여부만 확인하면 되므로 findById 사용)
            Post post = postRepository.findById(reqDto.getTargetId())
                    .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

            // ⚠️ 주의: 여기서 post.getLikeCount()를 쓰지 마세요. (옛날 값일 수 있음)

            // 이미 좋아요 눌렀는지 확인
            Optional<PostLike> existingLike = postLikeRepository.findByPostAndMember(post, member);

            if (existingLike.isPresent()) {
                // [취소 로직]
                postLikeRepository.delete(existingLike.get());

                // 1. DB 업데이트 (원자적 감소)
                postRepository.decreaseLikeCount(post.getId());
                isLiked = false;
            } else {
                // [등록 로직]
                postLikeRepository.save(new PostLike(post, member));

                // 1. DB 업데이트 (원자적 증가)
                postRepository.increaseLikeCount(post.getId());
                isLiked = true;
            }

            // 2. 🔥 [중요] 업데이트 된 DB 값을 다시 조회해서 리턴 (ClearAutomatically 때문에 DB 찌름)
            // 자바 메모리 계산(post.getLikeCount + 1)은 동시성 상황에서 부정확함
            currentCount = postRepository.findLikeCountById(post.getId());

        } else if ("REPLY".equalsIgnoreCase(reqDto.getTargetType())) {
            // 댓글 조회
            Reply reply = replyRepository.findById(reqDto.getTargetId()).orElseThrow(() -> new IllegalArgumentException("댓글 없음"));

            // 좋아요 상태 확인
            Optional<ReplyLike> existingLike = replyLikeRepository.findByReplyAndMember(reply, member);

            if (existingLike.isPresent()) {
                // 좋아요 삭제 -> 카운트 감소
                replyLikeRepository.delete(existingLike.get());
                replyRepository.decreaseLikeCount(reply.getId());
                isLiked = false;
            } else {
                // 좋아요 저장 -> 카운트 증가
                replyLikeRepository.save(new ReplyLike(reply, member));
                replyRepository.increaseLikeCount(reply.getId());
                isLiked = true;
            }

            // 좋아요 갯수 db에서 조회
            currentCount = replyRepository.findLikeCountById(reply.getId());
        } else {
            throw new IllegalArgumentException("잘못된 대상");
        }

        // null 방지
        if (currentCount == null) currentCount = 0;

        log.info("좋아요 처리 완료 - liked: {}, count: {}", isLiked, currentCount);

        return LikeResDto.builder()
                .liked(isLiked)
                .likeCount(Long.valueOf(currentCount)) // DTO 타입에 맞춰 변환
                .build();
    }
}