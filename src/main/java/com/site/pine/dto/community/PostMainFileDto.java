package com.site.pine.dto.community;

import lombok.Getter;

@Getter
public class PostMainFileDto {

    private Long postId;
    private Long fileId;
    private String path;

    public PostMainFileDto(Long postId, Long fileId, String path) {
        this.postId = postId;
        this.fileId = fileId;
        this.path = path;
    }
}
