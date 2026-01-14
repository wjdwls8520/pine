package com.site.pine.repository.like;

import com.site.pine.entity.Member;
import com.site.pine.entity.like.PostLike;
import com.site.pine.entity.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // 1. 특정 사용자가 특정 게시글에 좋아요 눌렀는지 확인
    Optional<PostLike> findByPostAndMember(Post post, Member member);

    boolean existsByPost_IdAndMember_Id(Long postId, Long memberId);

    void deleteByPost(Post post);

    @Modifying
    @Query("DELETE FROM PostLike pl WHERE pl.post = :post")
    void deleteAllByPost(Post post);

    // 마이페이지: 내가 좋아요한 게시글 목록 조회 (N+1 방지)
    @Query(value = """
        SELECT pl.post FROM PostLike pl
        JOIN pl.post p
        JOIN p.member m
        WHERE pl.member.id = :memberId
        ORDER BY pl.likedate DESC
    """,
    countQuery = """
        SELECT COUNT(pl) FROM PostLike pl
        WHERE pl.member.id = :memberId
    """)
    Page<Post> findPostsByMemberLikes(@Param("memberId") Long memberId, Pageable pageable);
}
