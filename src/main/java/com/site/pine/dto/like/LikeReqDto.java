package com.site.pine.dto.like;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LikeReqDto {
    private String targetType; // "POST" 또는 "REPLY"등등
    private Long targetId;     // 게시글 ID 또는 댓글 ID
}
