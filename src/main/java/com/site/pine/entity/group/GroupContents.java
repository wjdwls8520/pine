package com.site.pine.entity.group;

import com.site.pine.entity.File;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class GroupContents {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("그룹명")
    @Column(nullable = false)
    private String groupName;

    @Comment("그룹의 소개글")
    @Column(nullable = false)
    private String groupDescription;

    @Comment("그룹의 가입 승인 여부, 기본값은 1")
    @Column(nullable = false)
    private Integer joinState = 1;

    @Comment("그룹가입 자동 승인 여부, 기본값은 1")
    @Column(nullable = false)
    private Integer autoJoin = 1;

    @Comment("그룹의 가입 승인 여부, 기본값은 10명")
    @Column(nullable = false)
    private Integer userLimit = 10;

    @Comment("오늘 하루 방문자 수")
    @Column(nullable = false)
    private Integer todayViewCount = 0;

    @Comment("총 방문자 수")
    @Column(nullable = false)
    private Integer allViewCount = 0;

    @Comment("좋아요 수")
    @Column(nullable = false)
    private Integer likeCount = 0;

    @Comment("게시글 수")
    @Column(nullable = false)
    private Integer postCount = 0;

    @Comment("그룹 멤버 수")
    @Column(nullable = false)
    private Integer groupMemberCount = 0;

    @Comment("그룹 생성 날짜")
    @CreationTimestamp
    private Timestamp indate;

    @Comment("그룹 대표 이미지 1장")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "file_id", nullable = false)
    private File file;

}
