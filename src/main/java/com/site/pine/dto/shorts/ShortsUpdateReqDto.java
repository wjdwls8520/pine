package com.site.pine.dto.shorts;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@ToString
public class ShortsUpdateReqDto {
    private Long postId;
    @NotBlank(message = "제목은 필수입니다.")
    private String title;
    @NotBlank(message = "내용은 필수입니다.")
    private String content;
    private List<String> tags;

    private MultipartFile thumbnailFile;
    private String thumbnailType;
}
