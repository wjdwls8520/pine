package com.site.pine.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.site.pine.dto.group.GroupCategoryDto;
import com.site.pine.dto.group.GroupContentReqDto;
import com.site.pine.dto.group.GroupContentResDto;
import com.site.pine.entity.File;
import com.site.pine.entity.group.GroupCategoryList;
import com.site.pine.entity.group.GroupContents;
import com.site.pine.entity.group.GroupInCategory;
import com.site.pine.entity.post.Post;
import com.site.pine.mapper.GroupMapper;
import com.site.pine.repository.FileRepository;
import com.site.pine.repository.group.GroupContentsRepository;
import com.site.pine.repository.group.GroupCategoryRepository;
import com.site.pine.repository.group.GroupInCategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

    private final GroupMapper gm;

    // 태초에 db에 저장되는 카테고리들을 가져옴
    public List<GroupCategoryDto> getCategory() {
        // 빈배열 생성
        List<GroupCategoryDto> result = new ArrayList<>();

        // 태초에 db에 저장되는 카테고리들을 모두 가져옴
        List<GroupCategoryList> categorys = gcr.findAll();

        // 태초 db 카테고리리스트의 수만큼 for 반복
        for(GroupCategoryList category: categorys) {
            // 응답DTO에 값을 넣는 과정을 mapper로 제작
            GroupCategoryDto resDto = gm.toGroupCategoryListResDto(category);
            // mapper로 제작돤 값을 빈배열에 추가
            result.add(resDto);
        }
        return result;
    }

    // group페이지의 메인에서 모든 그룹을 보여줌
    public HashMap<String, Object> getAllGroups(Integer page) {
        HashMap<String, Object> result = new HashMap<>();
        // 빈 배열 생성
        List<GroupContentResDto> list = new ArrayList<>();

        // 모든 그룹을 리스트로 조회
        Pageable pageable = PageRequest.of(page, 6);
        Page<GroupContents> groupContentsE_List = gconr.findAllByOrderByIndateDesc(pageable);
        // 조회한 그룹이 없다면 리턴
        if(groupContentsE_List.isEmpty()) {
            result.put("msg", "조회된 그룹이 없습니다.");
            return result;
        }

        // 조회한 그룹이 있다면 리턴
        for(GroupContents groupContentsE : groupContentsE_List) {
            // resDto로 매퍼 제작
            GroupContentResDto resDto = gm.toGroupContentResDto(groupContentsE);

            list.add(resDto);
        }
        result.put("groupList", list);
        result.put("totalPage", groupContentsE_List.getTotalPages());
        return result;
    }

    public void insertGroupContent(GroupContentReqDto groupContentReqDto) throws IOException {
        // 그룹엔티티에 값을 세터
        GroupContents groupContentsE = new GroupContents();
        groupContentsE.setGroupName(groupContentReqDto.getGroupName());
        groupContentsE.setGroupDescription(groupContentReqDto.getGroupDescription());
        groupContentsE.setJoinState(groupContentReqDto.getJoinState());
        groupContentsE.setAutoJoin(groupContentReqDto.getAutoJoin());
        groupContentsE.setUserLimit(groupContentReqDto.getUserLimit());

        // 멀티파트파일의 기본 속성들 사용 + sus(s3)서비스에서 임의값을 추가한 파일 저장
        String filePageType = "groupBanner";
        String originalFileName = groupContentReqDto.getGroupImg().getOriginalFilename();
        Long fileSize = groupContentReqDto.getGroupImg().getSize();
        String filePath = sus.saveFile(groupContentReqDto.getGroupImg());
        String fileContentType = groupContentReqDto.getGroupImg().getContentType();
        // 파일엔티티에 위의 값들을 세터
        File fileEntity = new File();
        fileEntity.setPageType(filePageType);
        fileEntity.setOriginalname(originalFileName);
        fileEntity.setSize(fileSize);
        fileEntity.setPath(filePath);
        fileEntity.setContentType(fileContentType);

        // 그룹에 해당 파일 엔티티를 연결 ( 파일과 원투원관계로 케스케이드 all 설정 )
        groupContentsE.setFile(fileEntity);
        // 그룹엔티티와 연결된 파일엔티티를 같이 저장함
        gconr.save(groupContentsE);

        // 태초에 db에 저장된 카테고리 정보들을 조회
        List<GroupCategoryList> allInitCategory = gcr.findAll();
        // 내가 클라이언트한테 받은 선택된 카테고리 개수만큼 for 반복
        for( Integer categoryId : groupContentReqDto.getCategoryIds()) {
            // 서버에서 태초부터 생긴 카테고리의 값을 필터로 현재 내가 선택한 카테고리랑 같은 catrgoryId이면 그에 맞는 name을 저장
            GroupCategoryList matchedCategory = allInitCategory.stream()
                    .filter(cat -> cat.getId().equals(categoryId))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("카테고리를 찾을 수 없습니다. ID: " + categoryId));

            GroupInCategory groupInCategory = new GroupInCategory();
            groupInCategory.setCategoryId(categoryId);
            groupInCategory.setCategoryNameKor(matchedCategory.getNameKor());
            groupInCategory.setCategoryNameEng(matchedCategory.getNameEng());
            groupInCategory.setGroupContents(groupContentsE);
            // 선택된 카테고리 아이디들을 저장함.
            gicr.save(groupInCategory);
        }

    }

    // 하나의 그룹 디테일
    public void getGroup(Long id) throws IllegalAccessException {
        GroupContents groupDetailE = gconr.findById(id).orElseThrow(()-> new IllegalAccessException("존재하지 않는 그룹입니다."));

    }
}
