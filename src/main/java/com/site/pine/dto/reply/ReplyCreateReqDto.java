package com.site.pine.dto.reply;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReplyCreateReqDto {

    private Long postId;
    @NotBlank
    @Size(max = 500, message = "댓글은 500자를 초과할 수 없습니다.")
    private String content;
    private Long parentId;    // null = 댓글, 값 있으면 대댓글

}
