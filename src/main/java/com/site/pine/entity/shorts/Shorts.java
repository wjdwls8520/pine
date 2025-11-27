package com.site.pine.entity.shorts;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Data
public class Shorts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("쇼츠 고유번호")
    private Long id;

    @Comment("쇼츠 내용")
    @Column(nullable = false, length = 200)
    private String content;

    @Comment("작성 날짜")
    @CreationTimestamp
    private Timestamp writeDate;

    @Comment("수정 날짜")
    @UpdateTimestamp
    private Timestamp updateDate;


//    @Comment("회원 연관관계")
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "id")
//    private Member member;
}
