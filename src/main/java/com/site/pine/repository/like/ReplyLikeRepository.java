package com.site.pine.repository.like;

import com.site.pine.entity.Member;
import com.site.pine.entity.Reply;
import com.site.pine.entity.like.ReplyLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReplyLikeRepository extends JpaRepository<ReplyLike, Long> {

    Optional<ReplyLike> findByReplyAndMember(Reply reply, Member member);
    long countByReply(Reply reply);

    // clearAutomatically = true: 벌크 연산 후 영속성 컨텍스트(1차 캐시)를 비워줌 (데이터 불일치 방지)
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ReplyLike rl WHERE rl.reply = :reply")
    void deleteByReply(@Param("reply") Reply reply);

    // 게시글 ID를 기준으로, 그 글에 달린 모든 댓글의 좋아요를 한 번에 삭제
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from ReplyLike rl where rl.reply.id in (select r.id from Reply r where r.post.id = :postId)")
    void deleteAllByPostId(@Param("postId") Long postId);
}
