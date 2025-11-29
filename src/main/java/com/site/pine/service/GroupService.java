package com.site.pine.service;

import com.site.pine.dto.group.GroupCategoryDto;
import com.site.pine.dto.group.GroupContentReqDto;
import com.site.pine.entity.File;
import com.site.pine.entity.group.GroupCategoryList;
import com.site.pine.entity.group.GroupContents;
import com.site.pine.entity.group.GroupInCategory;
import com.site.pine.repository.FileRepository;
import com.site.pine.repository.group.GroupContentsRepository;
import com.site.pine.repository.group.GroupCategoryRepository;
import com.site.pine.repository.group.GroupInCategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupService {

    private final GroupCategoryRepository gcr;
    private final GroupContentsRepository gconr;
    private final GroupInCategoryRepository gicr;

    private final S3UploadService sus;
    private final FileRepository frs;

    public List<GroupCategoryDto> getCategory() {
        List<GroupCategoryDto> result = new ArrayList<>();

        List<GroupCategoryList> categorys = gcr.findAll();

        for(GroupCategoryList category: categorys) {
            GroupCategoryDto resDto = new GroupCategoryDto();
            resDto.setId(category.getId());
            resDto.setNameKor(category.getNameKor());
            resDto.setNameEng(category.getNameEng());
            result.add(resDto);
        }

        return result;

    }

    public void insertGroupContent(GroupContentReqDto groupContentReqDto) throws IOException {
        GroupContents groupContentsE = new GroupContents();
        groupContentsE.setGroupName(groupContentReqDto.getGroupName());
        groupContentsE.setGroupDescription(groupContentReqDto.getGroupDescription());
        groupContentsE.setJoinState(groupContentReqDto.getJoinState());
        groupContentsE.setAutoJoin(groupContentReqDto.getAutoJoin());
        groupContentsE.setUserLimit(groupContentReqDto.getUserLimit());
        gconr.save(groupContentsE);

        for( Integer categoryId : groupContentReqDto.getCategoryIds()) {
            GroupInCategory groupInCategory = new GroupInCategory();
            groupInCategory.setCategoryId(categoryId);
            groupInCategory.setGroupContents(groupContentsE);
            gicr.save(groupInCategory);
        }

        // 원래 파일 이름
        String filePageType = "groupBanner";
        String originalFileName = groupContentReqDto.getGroupImg().getOriginalFilename();
        Long fileSize = groupContentReqDto.getGroupImg().getSize();
        String filePath = sus.saveFile(groupContentReqDto.getGroupImg());
        String fileContentType = groupContentReqDto.getGroupImg().getContentType();

        File fileEntity = new File();
        fileEntity.setPageType(filePageType);
        fileEntity.setOriginalname(originalFileName);
        fileEntity.setSize(fileSize);
        fileEntity.setPath(filePath);
        fileEntity.setContentType(fileContentType);

        frs.save(fileEntity);

    }
}
