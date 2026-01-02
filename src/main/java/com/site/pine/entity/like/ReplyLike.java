package com.site.pine.entity.like;

import com.site.pine.entity.Member;
import com.site.pine.entity.Reply;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reply_like", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_reply_like_member_reply",
                columnNames = {"member_id", "reply_id"}
        )
})
public class ReplyLike extends BaseLike {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id", nullable = false)
    private Reply reply;

    public ReplyLike(Reply reply, Member member) {
        this.reply = reply;
        super.setMember(member);
    }

}
