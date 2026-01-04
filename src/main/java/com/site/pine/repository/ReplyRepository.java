package com.site.pine.repository;

import com.site.pine.entity.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
        order by r.id desc
    """)
    List<Reply> findChildReplies(@Param("parentId") Long parentId);
}
