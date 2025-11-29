package com.site.pine.dto.group;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class GroupContentReqDto {
    private String groupName;
    private String groupDescription;
    private Integer joinState;
    private Integer autoJoin;
    private Integer userLimit;

    // 카테고리
    private List<Integer> categoryIds;

    // 파일
    private MultipartFile groupImg;
}
