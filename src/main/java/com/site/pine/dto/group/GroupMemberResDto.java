package com.site.pine.dto.group;

import com.site.pine.dto.member.MemberDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
public class GroupMemberResDto {
    private Long id;
    private MemberDto member;
    private Timestamp joinTime;
    private Integer role;

    private Long memberId;
    private String nickname;
    private String profileImg;
    private String profileMsg;

    private Long groupId;

    public GroupMemberResDto(Long id, Long memberId, String nickname, String profileImg, String profileMsg, Long groupId, Timestamp joinTime, Integer role) {
        this.id = id;
        this.memberId = memberId;
        this.nickname = nickname;
        this.profileImg = profileImg;
        this.profileMsg = profileMsg;
        this.groupId = groupId;
        this.joinTime = joinTime;
        this.role = role;
    }
}

