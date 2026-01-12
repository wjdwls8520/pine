package com.site.pine.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

// MemberPostItemDto.java (새로 생성)
@Getter
@AllArgsConstructor
public class MemberPostItemDto {
    private Long postId;
    private Integer category;
    private String content;
    private String fileSrc;
}
