package com.site.pine.entity;

import com.site.pine.enums.PageType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("내용")
    @Column(nullable = false, length = 500)
    private String content;

    @Comment("페이지타입 enum")
    @Enumerated(EnumType.STRING)
    private PageType targetType;

    @Comment("해당 컨텐츠의 id")
    @Column(nullable = false)
    private Long targetId;

    @Comment("일반=0,비공개(암호)=1,삭제=2,신고됨=3")
    @Column(nullable = false)
    private Integer status = 0;

    @Comment("작성 날짜")
    @CreationTimestamp
    private Timestamp writeDate;

    @Comment("수정 날짜")
    @UpdateTimestamp
    private Timestamp updateDate;




    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Comment("부모댓글")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Reply parent;

    @Comment("자식댓글들(부모댓삭제해도 남아있음)")
    @OneToMany(mappedBy = "parent")
    private List<Reply> children = new ArrayList<>();


}
