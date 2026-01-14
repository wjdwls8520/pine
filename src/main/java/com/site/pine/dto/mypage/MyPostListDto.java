package com.site.pine.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyPostListDto {
    
    private Long postId;
    
    private String title;
    
    private String content;
    
    private String thumbnailImage;
    
    private String postType;
    
    private Long viewCount;
    
    private Integer likeCount;
    
    private Integer replyCount;
    
    private Timestamp writeDate;

    private Long groupId;
}

