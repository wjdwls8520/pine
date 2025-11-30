package com.site.pine.dto.community;

import com.site.pine.dto.FileDto;
import com.site.pine.entity.File;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.List;

@Data
public class PostResDto {

    private Long id;

    private String content;

    private Integer likeCount;

    private Integer replyCount;

    private Integer status;

    private Integer category;

    private Timestamp writeDate;

    private Timestamp updateDate;

    // 파일
    private List<FileDto> file;
}
