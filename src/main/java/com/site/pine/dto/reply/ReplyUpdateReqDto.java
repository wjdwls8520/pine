package com.site.pine.dto.reply;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReplyUpdateReqDto {
        private Long replyId;
        private String content;
}
