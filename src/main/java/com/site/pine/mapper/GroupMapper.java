package com.site.pine.mapper;

import com.site.pine.dto.group.GroupCategoryDto;
import com.site.pine.dto.group.GroupContentResDto;
import com.site.pine.entity.group.GroupCategoryList;
import com.site.pine.entity.group.GroupContents;
import org.springframework.stereotype.Component;

@Component
public class GroupMapper {

    public GroupCategoryDto toGroupCategoryListResDto(GroupCategoryList category) {
        GroupCategoryDto resDto = new GroupCategoryDto();
        resDto.setId(category.getId());
        resDto.setNameKor(category.getNameKor());
        resDto.setNameEng(category.getNameEng());

        return resDto;
    };

    public GroupContentResDto toGroupContentResDto(GroupContents groupContentsE) {
        GroupContentResDto resDto = new GroupContentResDto();
        resDto.setId(groupContentsE.getId());
        resDto.setGroupName(groupContentsE.getGroupName());
        resDto.setGroupDescription(groupContentsE.getGroupDescription());
        resDto.setJoinState(groupContentsE.getJoinState());
        resDto.setAutoJoin(groupContentsE.getAutoJoin());
        resDto.setUserLimit(groupContentsE.getUserLimit());

        resDto.setAllViewCount(groupContentsE.getAllViewCount());
        resDto.setTodayViewCount(groupContentsE.getTodayViewCount());
        resDto.setLikeCount(groupContentsE.getLikeCount());
        resDto.setPostCount(groupContentsE.getPostCount());
        resDto.setGroupMemberCount(groupContentsE.getGroupMemberCount());

        resDto.setIndate(groupContentsE.getIndate());

        resDto.setGroupImg(groupContentsE.getFile());
        return resDto;
    }
}
