package com.site.pine.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Likes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("컨텐츠 타입(Post, Reply 등)")
    @Column(nullable = false)
    private int targetType;

    @Comment("해당 컨텐츠의 id")
    @Column(nullable = false)
    private Long targetId;

    @Comment("좋아요날짜")
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Timestamp likedate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}
