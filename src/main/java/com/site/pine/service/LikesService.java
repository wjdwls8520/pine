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
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        boolean isLiked = false;
        long currentCount = 0;

        // 1. 게시글(POST)인 경우
        if ("POST".equalsIgnoreCase(reqDto.getTargetType())) {

            Post post = postRepository.findById(reqDto.getTargetId())
                    .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

            Optional<PostLike> existingLike = postLikeRepository.findByPostAndMember(post, member);

            if (existingLike.isPresent()) {
                postLikeRepository.delete(existingLike.get()); // 삭제
                isLiked = false;
            } else {
                postLikeRepository.save(new PostLike(post, member)); // 저장
                isLiked = true;
            }

            // 🔥 [핵심] DB에서 진짜 개수를 다시 세온다!
            currentCount = postLikeRepository.countByPost(post);

            // (선택사항) Post 엔티티에도 업데이트하고 싶다면:
             post.setLikeCount((int) currentCount);

            // 2. 댓글(REPLY)인 경우
        } else if ("REPLY".equalsIgnoreCase(reqDto.getTargetType())) {

            Reply reply = replyRepository.findById(reqDto.getTargetId())
                    .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

            Optional<ReplyLike> existingLike = replyLikeRepository.findByReplyAndMember(reply, member);

            if (existingLike.isPresent()) {
                replyLikeRepository.delete(existingLike.get());
                isLiked = false;
            } else {
                replyLikeRepository.save(new ReplyLike(reply, member));
                isLiked = true;
            }

            // 🔥 [핵심] 댓글 개수도 다시 센다!
            currentCount = replyLikeRepository.countByReply(reply);

        } else {
            throw new IllegalArgumentException("잘못된 대상입니다.");
        }

        // 3. 결과 리턴 (여기에 likeCount가 꼭 있어야 함)
        return LikeResDto.builder()
                .liked(isLiked)
                .likeCount(currentCount) // 👈 JS가 이 값을 기다리고 있음!
                .build();
    }
}
