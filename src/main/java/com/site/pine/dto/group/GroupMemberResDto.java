package com.site.pine.dto.group;

import com.site.pine.dto.member.MemberDto;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class GroupMemberResDto {
    private Long id;
    private MemberDto member;
    private Timestamp joinTime;
    private Integer role;
}
