package com.site.pine.dto.like;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikeResDto {
    private boolean liked;   // 좋아요 눌린 상태면 true, 아니면 false
    private long likeCount;  // 최신 좋아요 개수
}
