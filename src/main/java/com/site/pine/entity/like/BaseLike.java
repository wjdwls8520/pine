package com.site.pine.entity.like;

import com.site.pine.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;

@MappedSuperclass
@Getter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("좋아요날짜")
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Timestamp likedate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 상속받은 자식 클래스에서 멤버를 세팅할 수 있게 protected로 선언
    protected void setMember(Member member) {
        this.member = member;
    }
}
