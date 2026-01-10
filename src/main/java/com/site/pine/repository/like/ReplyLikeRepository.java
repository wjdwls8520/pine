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
}
