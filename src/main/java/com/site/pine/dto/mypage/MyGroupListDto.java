package com.site.pine.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyGroupListDto {
    
    private Long groupId;
    
    private String groupName;
    
    private String groupImage;
    
    private Long totalMemberCount;
    
    private String myRole;
}

