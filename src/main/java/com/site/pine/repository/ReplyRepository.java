package com.site.pine.repository;

import com.site.pine.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    // 부모 댓글만
    @Query("""
    select r from Reply r
    left join fetch r.children
    where r.targetType = :type
    and r.targetId = :targetId
    and r.parent is null
    order by r.writeDate asc
    """)
    List<Reply> findParentReplies(int type, Long targetId);

}
