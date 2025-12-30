package com.site.pine.entity.community;

import com.site.pine.entity.post.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Setter
public class CommunityPost {
    @Id
    private Long postId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Comment("카테고리, (기본)자게 1/ kpop 2")
    @Column(nullable = false)
    private Integer category = 1;
}
