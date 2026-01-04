package com.site.pine.repository;

import com.site.pine.entity.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


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

}
