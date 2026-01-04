package com.site.pine.dto.group;

import lombok.*;
import java.sql.Timestamp; // 가능하다면 LocalDateTime 권장

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupJoinResDto {
    private Long id;

    private Long groupId;
    private String groupName;

    private Long memberId;
    private String userId;
    private String nickName;
    private String profileImg;

    private String introduction;
    private Integer status; // "신청 상태: 0=대기, 1=승인, 2=거절"
    private Timestamp requestDate;

    // JPQL용 public 생성자 (순서가 매우 중요함!)
    public GroupJoinResDto(
            Long id,
            Long groupId,
            String groupName,
            Long memberId,
            String userId,
            String nickName,
            String profileImg,
            String introduction,
            Integer status,
            Timestamp requestDate
    ) {
        this.id = id;
        this.groupId = groupId;
        this.groupName = groupName;
        this.memberId = memberId;
        this.userId = userId;
        this.nickName = nickName;
        this.profileImg = profileImg;
        this.introduction = introduction;
        this.status = status;
        this.requestDate = requestDate;
    }
}