package com.site.pine.dto.shorts;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class ShortsUploadReqDto {
    private String title;
    private String content;

//    private List<MultipartFile> files;
    // 썸네일 자동/수동 구분
    private String thumbnailType;

    private MultipartFile videoFile;      // 영상
    private MultipartFile thumbnailFile;  // 썸네일

}
