package com.site.pine.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PageType {
    COMMUNITY("커뮤니티"),
    GROUP("그룹 배너"),
    GROUP_POST("그룹 게시글"),
    SHORTS("쇼츠"),
    SHORTS_THUMBNAIL("쇼츠 썸네일");

    private final String label;
}
