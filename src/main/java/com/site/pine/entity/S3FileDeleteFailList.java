package com.site.pine.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
public class S3FileDeleteFailList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Comment("에러 메세지")
    @Column(nullable = false)
    private String errorMessage;

    @CreationTimestamp
    private Timestamp indate;
}
