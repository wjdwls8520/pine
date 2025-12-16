package com.site.pine.entity.group;

import com.site.pine.entity.File;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupContents {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("그룹명")
    @Column(nullable = false, length = 100)
    private String groupName;

    @Comment("그룹의 소개글")
    @Column(nullable = false, length = 1000)
    private String groupDescription;

    @Comment("그룹의 가입 승인 여부, 기본값은 1, 가입가능 = 1, 가입불가능 = 0")
    @Column(nullable = false)
    private Integer joinState = 1;

    @Comment("그룹가입 자동 승인 여부, 기본값은 1, 자동승인 1, 관리자승인 0 ")
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
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "file_id", nullable = false)
    private File file;

    @Comment("그룹에 해당하는 카테고리")
    @OneToMany(
            mappedBy = "groupContents",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<GroupInCategory> categoryIds = new ArrayList<>();

    @Comment("그룹에 가입된 멤버")
    @OneToMany(
            mappedBy = "groupContents",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<GroupMember> groupMembers = new ArrayList<>();
}
