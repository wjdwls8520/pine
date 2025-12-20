package com.site.pine.entity.post;

import com.site.pine.entity.File;
import com.site.pine.entity.Member;
import com.site.pine.entity.Tag;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("포스트 고유번호")
    private Long id;

    @Comment("포스트 내용")
    @Column(nullable = false)
    private String content;

    @Comment("좋아요 수, 기본값 0")
    @Column(nullable = false)
    private Integer likeCount = 0;

    @Comment("댓글 수, 기본값 0")
    @Column(nullable = false)
    private Integer replyCount = 0;

    @Comment("공개 비공개, 공개 0 / 비공개 1")
    @Column(nullable = false)
    private Integer status = 0;

    @Comment("카테고리, (기본)자게 1/ kpop 2")
    @Column(nullable = false)
    private Integer category = 1;

    @Comment("작성 날짜")
    @CreationTimestamp
    private Timestamp writeDate;

    @Comment("수정 날짜")
    @UpdateTimestamp
    private Timestamp updateDate;

    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<File> files = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;


}
//OneToMany
//eager
//
//        postRepository.findAll();
//        post ~~~~~~
//            file ~~~~~~
//
//lazy
//        postRepository.findAll();
//            post ~~~~~~
//
//
//
//        postRepository.findAll().getTItle();
//        postRepository.findAll().getFiles();
