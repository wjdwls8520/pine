package com.site.pine.dto.community;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class PostReqDto {
    private Integer category;
    private String postBody;
    private String mediaJson;
    private List<MultipartFile> files;  // 다중 파일 업로드
    private Integer status;
}
