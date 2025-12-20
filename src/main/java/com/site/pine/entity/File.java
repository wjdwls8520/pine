package com.site.pine.entity;

import com.site.pine.entity.post.Post;
import com.site.pine.entity.shorts.Shorts;
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

    @Comment("파일의 페이지 타입 ex) community, groupBanner. groupPost, shorts, shortsThumbnail")
    @Column(nullable = false)
    private String pageType;

    @Comment("파일의 원래이름")
    @Column(nullable = false)
    private String originalname;

    @Comment("파일 크기")
    @Column(nullable = false)
    private Long size;

    @Comment("파일 경로")
    private String path;

    @Comment("파일 확장자")
    @Column(nullable = false)
    private String contentType;

    @CreationTimestamp
    private Timestamp indate;

    @Comment("파일 처리 상태 0:대기 1:처리중 2:완료 3:실패")
    @Column(nullable = false)
    private int status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Comment("쇼츠/썸내일 파일")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shorts_id")
    private Shorts shorts;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "groupPost_id")
//    private GroupPost groupPost;

}
