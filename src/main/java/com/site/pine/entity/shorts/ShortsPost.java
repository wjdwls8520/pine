package com.site.pine.entity.shorts;

import com.site.pine.entity.post.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Setter
public class ShortsPost {
    @Id
    private Long postId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Comment("쇼츠 제목")
    @Column(nullable = false, length = 100)
    private String title;
}
