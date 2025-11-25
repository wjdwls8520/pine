package com.site.pine.dto.community;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class PostResDto {

    private Long id;

    private String content;

    private Integer likeCount;

    private Integer replyCount;

    private Integer status;

    private Integer category;

    private String file;

    private Timestamp writeDate;

    private Timestamp updateDate;
}
