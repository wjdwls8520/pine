package com.site.pine.entity;

import com.site.pine.entity.post.Post;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Formula;

import java.time.LocalDateTime;
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

    // targetId 삭제 -> Post 엔티티와 직접 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Comment("일반글 = N, 삭제글 = Y")
    @Column(nullable = false)
    private String deleteYN = "N";

    @Comment("작성 날짜")
    @CreationTimestamp
    private LocalDateTime writeDate;

    @Comment("수정 날짜")
    @UpdateTimestamp
    private LocalDateTime updateDate;

    @Comment("좋아요 수, 기본값 0")
    @Column(nullable = false)
    private Integer likeCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Comment("부모댓글")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Reply parent;

    // db에 실제 칼럼 없음
    // 댓글 더보기 카운트
    @Basic(fetch = FetchType.LAZY)
    @Formula("(select count(1) from reply r where r.parent_id = id and r.deleteyn = 'N')")
    private int childCount;

    @Comment("대댓글(자식댓글)")
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @BatchSize(size = 100)
    @OrderBy("id DESC")
    private List<Reply> children = new ArrayList<>();


}
