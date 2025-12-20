package com.site.pine.mapper;

import com.site.pine.dto.FileDto;
import com.site.pine.dto.group.GroupCategoryDto;
import com.site.pine.dto.group.GroupContentResDto;
import com.site.pine.dto.group.GroupMemberResDto;
import com.site.pine.dto.member.MemberDto;
import com.site.pine.entity.group.GroupCategoryList;
import com.site.pine.entity.group.GroupContents;
import com.site.pine.entity.group.GroupInCategory;
import com.site.pine.entity.group.GroupMember;
import org.springframework.stereotype.Component;

@Component
public class GroupMapper {

    public GroupCategoryDto toGroupCategoryListResDto(GroupCategoryList category) {
        GroupCategoryDto resDto = new GroupCategoryDto();
        resDto.setCategoryId(category.getId());
        resDto.setNameKor(category.getNameKor());
        resDto.setNameEng(category.getNameEng());

        return resDto;
    };

    public GroupCategoryDto toGroupInCategoryResDto(GroupInCategory category) {
        GroupCategoryDto resDto = new GroupCategoryDto();
        resDto.setCategoryId(category.getCategoryId().getId());
        resDto.setNameKor(category.getCategoryId().getNameKor());
        resDto.setNameEng(category.getCategoryId().getNameEng());

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

        FileDto fileDto = new FileDto();
        fileDto.setPath(groupContentsE.getFile().getPath());

        resDto.setGroupImg(fileDto);
        return resDto;
    }

    public GroupMemberResDto toGroupMemberResDto(MemberDto memberdto, GroupMember groupMember) {
        GroupMemberResDto groupMemberResDto = new GroupMemberResDto();
        groupMemberResDto.setId(groupMember.getId());
        groupMemberResDto.setMember(memberdto);
        groupMemberResDto.setJoinTime(groupMember.getJoinTime());
        groupMemberResDto.setRole(groupMember.getRole());
        return groupMemberResDto;
    }
}
