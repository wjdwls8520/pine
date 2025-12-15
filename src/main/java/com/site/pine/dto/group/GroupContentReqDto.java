package com.site.pine.dto.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class GroupContentReqDto {
    @NotBlank
    @Size(max = 50)
    private String groupName;
    @NotBlank
    @Size(max = 500)
    private String groupDescription;
    private Integer joinState;
    private Integer autoJoin;
    private Integer userLimit;

    // 카테고리
    private List<Integer> categoryIds;

    // 파일
    private MultipartFile groupImg;
}
