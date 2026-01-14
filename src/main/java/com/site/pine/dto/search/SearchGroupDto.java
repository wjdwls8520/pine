package com.site.pine.dto.search;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor // 기본 생성자 필수
public class SearchGroupDto {
    private Long id;
    private String groupName;
    private String groupDescription;
    private String groupImage;

    private Long memberCount;
    private Long likeCount;

    // JPQL 생성자
    public SearchGroupDto(Long id, String groupName, String groupDescription, String groupImage, Long memberCount, Long likeCount) {
        this.id = id;
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.groupImage = groupImage;
        this.memberCount = memberCount;
        this.likeCount = likeCount;
    }
}