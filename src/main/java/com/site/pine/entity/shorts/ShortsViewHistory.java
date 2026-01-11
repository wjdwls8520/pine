package com.site.pine.entity.shorts;

import com.site.pine.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "shorts_view_history",
        indexes = {
                // 중복 조회 쿼리 속도를 위한 인덱스 (targetId + member + 시간)
                @Index(name = "idx_shorts_view_history_check", columnList = "target_id, member_id, viewed_at"),
                @Index(name = "idx_shorts_view_cookie_check", columnList = "target_id, viewer_cookie, viewed_at")
        }
)
public class ShortsViewHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("시청한 쇼츠 포스트")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", nullable = false)
    private ShortsPost targetId;

    @Comment("시청자 (회원)")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member viewer;

    @Comment("시청자 (비회원 쿠키)")
    @Column(name = "viewer_cookie")
    private String viewerCookie;

    @Comment("시청 일시")
    @CreatedDate
    @Column(name = "viewed_at", nullable = false, updatable = false)
    private LocalDateTime viewedAt;

    public ShortsViewHistory(ShortsPost targetId, Member viewer, String viewerCookie) {
        this.targetId = targetId;
        this.viewer = viewer;
        this.viewerCookie = viewerCookie;
    }
}