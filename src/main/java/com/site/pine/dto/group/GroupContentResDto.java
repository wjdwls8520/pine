package com.site.pine.dto.group;

import com.site.pine.dto.FileDto;
import com.site.pine.entity.File;
import com.site.pine.entity.group.GroupMember;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
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
    private List<GroupCategoryDto> categoryIds;

    // 파일
    private FileDto groupImg;

    // 그룹멤버
    private List<GroupMemberResDto> groupMembers;
}
