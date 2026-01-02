package com.site.pine.repository.like;

import com.site.pine.entity.Member;
import com.site.pine.entity.Reply;
import com.site.pine.entity.like.ReplyLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReplyLikeRepository extends JpaRepository<ReplyLike, Long> {

    Optional<ReplyLike> findByReplyAndMember(Reply reply, Member member);
    long countByReply(Reply reply);
}
