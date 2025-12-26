package com.site.pine.entity;

import com.site.pine.enums.PageType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(
        indexes = {
                @Index(
                        name = "idx_view_history_target_date",
                        columnList = "target_type, target_id, is_view"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_view_history_unique_view",
                        columnNames = {
                                "target_type",
                                "target_id",
                                "viewer_cookie",
                                "is_view"
                        }
                )
        }
)
public class ViewHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Comment("컨텐츠 타입(Post=1, Reply 등)")
    @Column(name = "target_type", nullable = false)
    private PageType targetType;

    @Comment("해당 컨텐츠의 id")
    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Comment("조회/방문 날짜 중복판단용")
    @Column(name = "is_view", nullable = false, updatable = false)
    private LocalDate isView;

    @Comment("조회/방문 날짜 통계용")
    @CreationTimestamp
    @Column(name = "viewdate", nullable = false, updatable = false)
    private Timestamp viewdate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member viewer;

    @Comment("비로그인 조회자")
    @Column(name = "viewer_cookie")
    private String viewerCookie;
}
