package com.site.pine.entity;

import com.site.pine.entity.post.Post;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Data
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("파일의 페이지 타입 ex) community, group, shorts")
    @Column(nullable = false)
    private String pageType;

    @Comment("파일의 원래이름")
    @Column(nullable = false)
    private String originalname;

    @Comment("파일 크기")
    @Column(nullable = false)
    private Long size;

    @Comment("파일 경로")
    @Column(nullable = false)
    private String path;

    @Comment("파일 확장자")
    @Column(nullable = false)
    private String contentType;

    @CreationTimestamp
    private Timestamp indate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post")
    private Post post;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "groupPost")
//    private GroupPost groupPost;

}
