package com.site.pine.dto.reply;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReplyCreateReqDto {

    private int targetType;   // 1 = post
    private Long targetId;    //
    private String content;
    private Long parentId;    // null = 댓글, 값 있으면 대댓글

}
