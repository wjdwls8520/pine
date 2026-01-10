package com.site.pine.entity.group;

import com.site.pine.entity.post.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Setter
public class GroupPost {
    @Id
    private Long postId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Comment("그룹아이디")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private GroupContents groupContents;
}
