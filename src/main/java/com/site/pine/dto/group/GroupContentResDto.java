package com.site.pine.dto.group;

import com.site.pine.entity.File;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.List;

@Data
public class GroupContentResDto {
    private Long id;
    private String groupName;
    private String groupDescription;
    private Integer joinState;
    private Integer autoJoin;
    private Integer userLimit;

    // reqDto에 없는 값들
    private Integer allViewCount;
    private Integer todayViewCount;
    private Integer likeCount;
    private Integer postCount;
    private Integer groupMemberCount;

    private Timestamp indate;

    // 카테고리
    private List<Integer> categoryIds;

    // 파일
    private File groupImg;
}
