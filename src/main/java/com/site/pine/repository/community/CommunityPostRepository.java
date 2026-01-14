package com.site.pine.repository.community;

import com.site.pine.dto.member.MemberAndCPostResDto;
import com.site.pine.entity.community.CommunityPost;
import com.site.pine.entity.post.Post;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.SQLOutput;
import java.util.List;
import java.util.Optional;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
    void deleteByPost(Post post);

    // [추가] 수정 페이지용: CommunityPost + Post 같이 가져오기 (성능 최적화)
    @Query("SELECT cp FROM CommunityPost cp JOIN FETCH cp.post WHERE cp.postId = :id")
    Optional<CommunityPost> findByIdWithPost(@Param("id") Long id);

    @Query("""
    SELECT new com.site.pine.dto.member.MemberAndCPostResDto(
        p.id,
        cp.category,
        p.content,
        (SELECT f.path FROM File f WHERE f.post = p ORDER BY f.id ASC LIMIT 1)
    )
    FROM CommunityPost cp
    JOIN cp.post p
    JOIN p.member m
    WHERE m.id = :id
    ORDER BY p.likeCount desc, p.writeDate desc
""")
    List<MemberAndCPostResDto> findRandomByMemberId(@Param("id") Long id, Pageable pageable);

    // 마이페이지: 내가 작성한 커뮤니티 게시글 조회 (N+1 방지)
    @Query(value = """
        SELECT cp FROM CommunityPost cp
        JOIN FETCH cp.post p
        JOIN FETCH p.member m
        WHERE m.id = :memberId
        ORDER BY p.writeDate DESC
    """,
    countQuery = """
        SELECT COUNT(cp) FROM CommunityPost cp
        JOIN cp.post p
        WHERE p.member.id = :memberId
    """)
    Page<CommunityPost> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);
}
