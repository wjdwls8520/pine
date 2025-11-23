package com.site.pine.service;

import com.site.pine.dto.group.GroupCategoryDto;
import com.site.pine.entity.group.GroupCategory;
import com.site.pine.repository.group.GroupCategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupService {

    private final GroupCategoryRepository gcr;

    public List<GroupCategoryDto> getCategory() {
        List<GroupCategoryDto> result = new ArrayList<>();

        List<GroupCategory> categorys = gcr.findAll();

        for(GroupCategory category: categorys) {
            GroupCategoryDto resDto = new GroupCategoryDto();
            resDto.setId(category.getId());
            resDto.setNameKor(category.getNameKor());
            resDto.setNameEng(category.getNameEng());
            result.add(resDto);
        }

        return result;

    }
}
