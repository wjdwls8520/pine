package com.site.pine.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyCommentListDto {
    
    private Long replyId;
    
    private String replyContent;
    
    private LocalDateTime replyWriteDate;
    
    private Long originalPostId;
    
    private String originalPostTitle;
    
    private String originalPostType;
}

