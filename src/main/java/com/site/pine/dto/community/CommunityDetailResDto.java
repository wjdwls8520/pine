package com.site.pine.dto.community;

import com.site.pine.dto.FileDto;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class CommunityDetailResDto {

    private Long id;
    private String content;
    private Integer likeCount;
    private boolean liked; //좋아요 여부
    private Integer replyCount;
    private Integer status;
    private Integer category;
    private Timestamp writeDate;
    private Timestamp updateDate;

    //멤버
    private String nickname;
    private String profile_img;

    // 파일
    private List<FileDto> files;


}
