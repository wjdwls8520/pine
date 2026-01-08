package com.site.pine.dto.group;

import lombok.Getter;

@Getter
public class GroupMemberLevelDto {
    private Long groupId;
    private Long memberId;
    private Integer role;
    private String answer;
}

