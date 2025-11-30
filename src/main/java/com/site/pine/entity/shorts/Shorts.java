package com.site.pine.entity.shorts;

import com.site.pine.entity.File;
import com.site.pine.entity.post.Post;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Shorts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("쇼츠 고유번호")
    private Long id;

    @Comment("쇼츠 제목")
    @Column(nullable = false, length = 100)
    private String title;

    @Comment("쇼츠 내용")
    @Column(nullable = false, length = 200)
    private String content;

    @Comment("작성 날짜")
    @CreationTimestamp
    private Timestamp indate;

    @Comment("수정 날짜")
    @UpdateTimestamp
    private Timestamp updateDate;

    @Comment("쇼츠/썸내일 파일")
    @OneToMany(
            mappedBy = "shorts",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<File> files = new ArrayList<>();

//    @Comment("회원 연관관계")
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "id")
//    private Member member;
}
