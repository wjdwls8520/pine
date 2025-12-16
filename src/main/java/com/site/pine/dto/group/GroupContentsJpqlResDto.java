package com.site.pine.dto.group;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class GroupContentsJpqlResDto {
    private Long id;
    private String groupName;
    private String groupDescription;
    private Integer joinState;
    private Integer autoJoin;
    private Integer userLimit;
    private Integer allViewCount;
    private Integer likeCount;
    private Integer postCount;
    private Integer groupMemberCount;
    private Integer todayViewCount;
    private Timestamp indate;

    private SimpleFileJpqlResDto groupImg;

    public GroupContentsJpqlResDto(
            Long id,
            String groupName,
            String groupDescription,
            Integer joinState,
            Integer autoJoin,
            Integer userLimit,
            Integer allViewCount,
            Integer likeCount,
            Integer postCount,
            Integer groupMemberCount,
            Integer todayViewCount,
            Timestamp indate,
            Long fileId,
            String filePath
    ) {
        this.id = id;
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.joinState = joinState;
        this.autoJoin = autoJoin;
        this.userLimit = userLimit;
        this.allViewCount = allViewCount;
        this.likeCount = likeCount;
        this.postCount = postCount;
        this.groupMemberCount = groupMemberCount;
        this.todayViewCount = todayViewCount;
        this.indate = indate;

        this.groupImg = (fileId == null)
                ? null
                : new SimpleFileJpqlResDto(fileId, filePath);
    }
}
