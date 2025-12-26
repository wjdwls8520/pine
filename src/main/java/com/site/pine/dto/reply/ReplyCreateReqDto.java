package com.site.pine.dto.reply;

import com.site.pine.enums.PageType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReplyCreateReqDto {

    private PageType targetType;   // enum PageType
    private Long targetId;    //
    private String content;
    private Long parentId;    // null = 댓글, 값 있으면 대댓글

}
