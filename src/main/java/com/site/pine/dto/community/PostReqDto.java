package com.site.pine.dto.community;

import lombok.Data;

@Data
public class PostReqDto {
    private Integer category;
    private String bodyHtml;
    private String bodyJson;
    private String mediaJson;
    private String file;     // 태그 input
    private Integer status;
}
