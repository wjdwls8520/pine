package com.site.pine.entity.like;

import com.site.pine.entity.Member;
import com.site.pine.entity.post.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post_like", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_post_like_member_post", // 제약조건 이름 (아무거나 상관없음)
                columnNames = {"member_id", "post_id"}
        )
})
public class PostLike extends  BaseLike{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    public PostLike(Post post, Member member) {
        this.post = post;
        super.setMember(member);
    }
}
