package com.site.pine.dto.shorts;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ShortsUploadReqDto {
    private String title;
    private String content;

    private List<MultipartFile> files;
    // 썸네일 자동/수동 구분
    private String thumbnailType;

}
