package com.site.pine.dto.community;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PostReqDto {
    private Integer category;
    private String postBody;
    private String mediaJson;
    private MultipartFile file;     // 태그 input
    private Integer status;
}
