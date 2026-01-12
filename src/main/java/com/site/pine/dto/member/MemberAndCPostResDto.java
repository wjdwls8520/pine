package com.site.pine.dto.member;

import lombok.Getter;

@Getter
public class MemberAndCPostResDto {
    private Long postId;
    private Integer category;
    private String postContent;
    private String fileSrc; // [수정] List<String> -> String (글 1개당 썸네일 1개)

    // 생성자 파라미터도 String으로 변경
    public MemberAndCPostResDto(Long postId, Integer category, String postContent, String fileSrc) {
        this.postId = postId;
        this.category = category;
        this.postContent = postContent;
        this.fileSrc = fileSrc;
    }
}