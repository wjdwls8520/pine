package com.site.pine.repository.group;

import com.site.pine.entity.group.GroupPost;
import com.site.pine.entity.post.Post;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GroupPostRepository extends JpaRepository<GroupPost, Long> {
    // [추가] 수정 페이지용: CommunityPost + Post 같이 가져오기 (성능 최적화)
    @Query("SELECT cp FROM GroupPost cp JOIN FETCH cp.post WHERE cp.postId = :id")
    Optional<GroupPost> findByIdWithPost(@Param("id") Long id);

    void deleteByPostId(Long postId);

    @Query(value = "SELECT gp FROM GroupPost gp JOIN FETCH gp.post p JOIN FETCH p.member m WHERE gp.groupContents.id = :groupId order by p.writeDate",
            countQuery = "SELECT count(gp) FROM GroupPost gp WHERE gp.groupContents.id = :groupId")
    Page<GroupPost> findByGroupPost(@Param("groupId") Long groupId, Pageable pageable);

    GroupPost findByPost(Post post);

    // 마이페이지: 내가 작성한 그룹 게시글 조회
    @Query(value = """
        SELECT gp FROM GroupPost gp
        JOIN FETCH gp.post p
        JOIN FETCH p.member m
        JOIN FETCH gp.groupContents gc
        WHERE m.id = :memberId
        ORDER BY p.writeDate DESC
    """,
            countQuery = """
        SELECT COUNT(gp) FROM GroupPost gp
        JOIN gp.post p
        WHERE p.member.id = :memberId
    """)
    Page<GroupPost> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    // 🔥 [핵심 수정] Post ID 리스트로 GroupPost 조회 (좋아요/댓글 탭 이동 문제 해결)
    // 기존: JOIN FETCH gp.groupContents 만 있었음 -> post 정보가 없어서 매핑 실패
    // 변경: JOIN FETCH gp.post 추가! (그래야 서비스에서 gp.getPost().getId()가 작동함)
    @Query("SELECT gp FROM GroupPost gp JOIN FETCH gp.groupContents JOIN FETCH gp.post WHERE gp.post.id IN :postIds")
    List<GroupPost> findByPostIdIn(@Param("postIds") List<Long> postIds);
}
