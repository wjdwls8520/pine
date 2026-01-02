package com.site.pine.repository.like;

import com.site.pine.entity.Member;
import com.site.pine.entity.like.PostLike;
import com.site.pine.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // 1. 특정 사용자가 특정 게시글에 좋아요 눌렀는지 확인
    Optional<PostLike> findByPostAndMember(Post post, Member member);

    // 2. 게시글의 총 좋아요 개수 세기
    long countByPost(Post post);

    boolean existsByPost_IdAndMember_Id(Long postId, Long memberId);

    void deleteByPost(Post post);
}
