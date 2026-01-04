package com.site.pine.dto.community;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@ToString
public class PostModifyDto {
    private String content;       // 본문
    private Integer category;     // 카테고리
    private Integer status;       // 공개/비공개 (0:공개, 1:비공개)
    private String tags;          // "태그1,태그2,태그3" (문자열로 옴)
    private List<Long> deleteFileIds; // 삭제할 파일 ID 리스트 (예: [10, 12])

    private List<MultipartFile> newFiles;
}