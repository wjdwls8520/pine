package com.site.pine.repository;

import com.site.pine.entity.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ReplyRepository extends JpaRepository<Reply, Long> {

    @Query("""
        select r 
        from Reply r 
        join fetch r.member 
        where r.post.id = :postId 
        and r.parent is null 
        and (
            r.deleteYN = 'N'
            or
            exists (select c from Reply c where c.parent = r and c.deleteYN = 'N')
        )
        order by r.writeDate desc
    """)
    Page<Reply> findParentReplies(@Param("postId") Long postId, Pageable pageable);

    // 특정 부모의 자식 댓글들만 조회 (N+1 방지 위해 fetch join)
    // 대댓글이 엄청 많지 않다면 List로, 너무 많으면 Pageable 적용 고려
    @Query("""
        select r 
        from Reply r 
        join fetch r.member 
        where r.parent.id = :parentId 
        and (r.deleteYN = 'N' or SIZE(r.children) > 0)
        order by r.id asc
    """)
    List<Reply> findChildReplies(@Param("parentId") Long parentId);

    // 1. 좋아요 개수 증가 (+1)
    // flushAutomatically = true: 쿼리 실행 전, 쌓여있는 insert/delete를 먼저 DB에 보냄
    // clearAutomatically = true: 쿼리 실행 후, 영속성 컨텍스트 비움 (데이터 싱크 맞춤)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Reply r set r.likeCount = r.likeCount + 1 where r.id = :id")
    void increaseLikeCount(@Param("id") Long id);

    // 2. 좋아요 개수 감소 (-1)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Reply r set r.likeCount = r.likeCount - 1 where r.id = :id")
    void decreaseLikeCount(@Param("id") Long id);

    // 3. 현재 좋아요 개수 조회
    @Query("select r.likeCount from Reply r where r.id = :id")
    Integer findLikeCountById(@Param("id") Long id);
}
