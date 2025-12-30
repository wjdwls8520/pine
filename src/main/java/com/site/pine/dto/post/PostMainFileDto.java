package com.site.pine.dto.post;

import lombok.Getter;

@Getter
public class PostMainFileDto {

    private Long postId;
    private Long fileId;
    private String path;
    private String contentType;
    private Integer status;

    public PostMainFileDto(Long postId, Long fileId, String path, String contentType, Integer status) {
        this.postId = postId;
        this.fileId = fileId;
        this.path = path;
        this.contentType = contentType;
        this.status = status;
    }
}
