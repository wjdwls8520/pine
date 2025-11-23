package com.site.pine.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

@Entity
@Data
public class GroupContents {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("그룹명")
    @Column(nullable = false)
    private String groupName;

    @Comment("그룹 대표 이미지")
    @Column(nullable = false)
    private String groupImg;

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
    private Integer userLimit = 10;


}
