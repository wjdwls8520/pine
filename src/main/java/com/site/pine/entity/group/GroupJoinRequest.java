package com.site.pine.entity.group;

import com.site.pine.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 1. 기본 생성자 보호
public class GroupJoinRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("가입 신청한 그룹")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private GroupContents groupContents;

    @Comment("가입 신청한 멤버")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Comment("가입 신청 메시지 (인사말 등)")
    @Column(length = 200)
    private String introduction;

    @Comment("신청 상태: 0=대기, 1=승인, 2=거절")
    @Column(nullable = false)
    private Integer status = 0;

    @CreationTimestamp
    @Comment("가입 신청 일시")
    private Timestamp requestDate;

    // 2. 생성자 및 빌더 패턴 적용
    @Builder
    public GroupJoinRequest(GroupContents groupContents, Member member, String introduction, Integer status) {
        this.groupContents = groupContents;
        this.member = member;
        this.introduction = introduction;
        this.status = status; // 초기 생성 시 상태는 무조건 '대기(0)'
    }

    // 3. 편의를 위한 정적 팩토리 메서드 (선택 사항)
    public static GroupJoinRequest create(GroupContents groupContents, Member member, String introduction) {
        return GroupJoinRequest.builder()
                .groupContents(groupContents)
                .member(member)
                .introduction(introduction)
                .build();
    }

    // 4. 비즈니스 로직 메서드 (Setter 대체)
    public void approve() {
        this.status = 1;
    }

    public void reject() {
        this.status = 2;
    }
}