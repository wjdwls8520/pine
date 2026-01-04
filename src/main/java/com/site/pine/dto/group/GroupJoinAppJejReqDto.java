package com.site.pine.dto.group;

import lombok.Getter;

// 그룹멤버신청에 대한 수락/거절에 관한 요청 dto
@Getter
public class GroupJoinAppJejReqDto {
    private String status;
    private Long groupId;
    private Long memberId;
}
