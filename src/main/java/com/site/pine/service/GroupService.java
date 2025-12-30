package com.site.pine.service;

import com.site.pine.dto.S3DeleteEventDto;
import com.site.pine.dto.group.*;
import com.site.pine.dto.member.MemberDto;
import com.site.pine.entity.File;
import com.site.pine.entity.Member;
import com.site.pine.entity.S3FileDeleteFailList;
import com.site.pine.entity.group.GroupCategoryList;
import com.site.pine.entity.group.GroupContents;
import com.site.pine.entity.group.GroupInCategory;
import com.site.pine.entity.group.GroupMember;
import com.site.pine.mapper.GroupMapper;
import com.site.pine.mapper.S3FileDeleteFailMapper;
import com.site.pine.repository.FileRepository;
import com.site.pine.repository.MemberRepository;
import com.site.pine.repository.ViewGroupRepository;
import com.site.pine.repository.group.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



@Slf4j
@Service
@RequiredArgsConstructor
public class GroupService {

    private final ApplicationEventPublisher applicationEventPublisher;

    private final GroupCategoryRepository gcr;
    private final GroupContentsRepository gconr;
    private final GroupInCategoryRepository gicr;

    private final MemberRepository mr;
    private final GroupMemberRepository gmr;

    private final ViewGroupRepository vr;

    private final S3UploadService sus;
    private final FileRepository fr;

    private final GroupMapper gm;

    private final S3FileDeleteFailListRepository sfdfr;
    private final S3FileDeleteFailMapper sfdfm;

    @PersistenceContext
    private EntityManager entityManager;


    // 태초에 db에 저장되는 카테고리들을 가져옴
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public HashMap<String, Object> getAllGroups(Integer page) {
        HashMap<String, Object> result = new HashMap<>();

        // 모든 그룹을 리스트로 조회
        Pageable pageable = PageRequest.of(page, 6);
        Page<GroupContentsJpqlResDto> groupContentsE_List = gconr.findGroupResDto(pageable);
        // Page<GroupContentsJpqlResDto> groupContentsE_List = gconr.findGroupResDto(11, pageable);

        // 조회한 그룹이 없다면 리턴
        if(groupContentsE_List.isEmpty()) {
            result.put("msg", "조회된 그룹이 없습니다.");
            return result;
        }

        result.put("groupList", groupContentsE_List.getContent());
        result.put("totalPage", groupContentsE_List.getTotalPages());
        return result;
    }

    @Transactional
    public void insertGroupContent(MemberDto memberdto, GroupContentReqDto groupContentReqDto) {
        String filePath = null;  // s3업로드가 될 변수 기본값은 null임으로 값이 null이면 s3 업로드가 실패했을 경우랑 같다.

        Member memberEntity = mr.findById(memberdto.getId()).orElseThrow(() -> new IllegalStateException("[error] 존재하지 않는 멤버 입니다.")); // 멤버조회 대상이 없을시 강제 에러실행.

        // 그룹엔티티에 값을 세터
        GroupContents groupContentsE = new GroupContents();
        groupContentsE.setGroupName(groupContentReqDto.getGroupName());
        groupContentsE.setGroupDescription(groupContentReqDto.getGroupDescription());
        groupContentsE.setJoinState(groupContentReqDto.getJoinState());
        groupContentsE.setAutoJoin(groupContentReqDto.getAutoJoin());
        groupContentsE.setUserLimit(groupContentReqDto.getUserLimit());



        // 멀티파트파일의 기본 속성들 사용 + sus(s3)서비스에서 임의값을 추가한 파일 저장
        String originalFileName = groupContentReqDto.getGroupImg().getOriginalFilename();
        Long fileSize = groupContentReqDto.getGroupImg().getSize();
        String fileContentType = groupContentReqDto.getGroupImg().getContentType();
        try {
            filePath = sus.saveFile(groupContentReqDto.getGroupImg());
        } catch (IOException e) {
            log.error("S3 업로드 실패", e);
            throw new IllegalStateException("파일 업로드에 실패했습니다."); // s3에서 에러가났을시 강제 에러실행.
        }
        // *** db 트랙잭셔널의 롤백현상을 감지하고 시작될 예약 클래스 ( s3 디티오를 스프링에게 알림 에러시 s3rollbacklistener 함수에서 스프링에서 이 디티오를 가져다가 사용함 )
        applicationEventPublisher.publishEvent(new S3DeleteEventDto(originalFileName, fileSize, filePath));

        // 파일엔티티에 위의 값들을 세터
        File fileEntity = new File();
        fileEntity.setOriginalname(originalFileName);
        fileEntity.setSize(fileSize);
        fileEntity.setPath(filePath);
        fileEntity.setContentType(fileContentType);
        // 그룹에 해당 파일 엔티티를 연결 ( 파일과 원투원관계로 케스케이드 all 설정 )
        groupContentsE.setFile(fileEntity);



        // 그룹멤버엔티티에 멤버엔티티와 그룹엔티티를 추가해서 그룹 -> 그룹멤버 <- 멤버 그룹멤버라는 중간연결엔티티를 제작
        GroupMember groupMemberEntity = new GroupMember();
        groupMemberEntity.setGroupContents(groupContentsE);
        groupMemberEntity.setMember(memberEntity);
        groupMemberEntity.setRole(1); // 그룹장
        groupContentsE.getGroupMembers().add(groupMemberEntity); // 그룹컨텐츠에 그룹멤버를 조인

        // 그룹멤버수 1추가 (그룹장)
        groupContentsE.setGroupMemberCount(groupContentsE.getGroupMemberCount() + 1);



        // 내가 클라이언트한테 받은 선택된 카테고리 개수만큼 for 반복
        for (Integer categoryId : groupContentReqDto.getCategoryIds()) {
            // 1) 카테고리 엔티티 조회
            GroupCategoryList categoryEntity = gcr.findById(categoryId)
                    .orElseThrow(() -> new IllegalStateException("존재하지 않는 카테고리입니다."));

            GroupInCategory groupInCategory = new GroupInCategory();
            groupInCategory.setCategoryId(categoryEntity);
            groupInCategory.setGroupContents(groupContentsE);
            groupContentsE.getCategoryIds().add(groupInCategory); // 그룹컨텐츠에 카테고리들을 조인
        }

        // 그룹엔티티와 연결된 파일엔티티 그리고 그룹멤버를 같이 저장함
        gconr.save(groupContentsE);
    }


    // 하나의 그룹 디테일
    @Transactional(readOnly = true)
    public GroupContentResDto getGroup(Long id) {

        GroupContents groupDetailE = gconr.findById(id).orElseThrow(()-> new IllegalStateException("존재하지 않는 그룹입니다."));

        GroupContentResDto groupContentResDto = gm.toGroupContentResDto(groupDetailE);

        List<GroupCategoryDto> categoryResult = new ArrayList<>();
        for (GroupInCategory category : groupDetailE.getCategoryIds()) {
            GroupCategoryDto categoryResDto = gm.toGroupInCategoryResDto(category);

            categoryResult.add(categoryResDto);
        }

        groupContentResDto.setCategoryIds(categoryResult);

        return groupContentResDto;
    }

    @Transactional(readOnly = true)
    public GroupMemberResDto getGroupMemberInfo(MemberDto memberdto, Long id) {
        GroupMemberResDto groupMemberResDto = null;

        if(memberdto != null) {
            GroupMember groupMember = gmr.findByMemberIdAndGroupId(memberdto.getId(), id).orElse(null);
            if(groupMember != null) { // 그룹 가입자인지 확인
                groupMemberResDto = gm.toGroupMemberResDto(memberdto, groupMember);
            }
        }
        return groupMemberResDto;
    }

    @Transactional
    public void updateGroupContent(MemberDto memberdto, Long groupId, GroupContentReqDto groupContentReqDto) {
        GroupContents groupContentsE = gconr.findById(groupId).orElseThrow(() -> new IllegalStateException("[error] 존재하지 않는 그룹입니다."));

        groupContentsE.setGroupName(groupContentReqDto.getGroupName());
        groupContentsE.setGroupDescription(groupContentReqDto.getGroupDescription());
        groupContentsE.setJoinState(groupContentReqDto.getJoinState());
        groupContentsE.setAutoJoin(groupContentReqDto.getAutoJoin());
        groupContentsE.setUserLimit(groupContentReqDto.getUserLimit());


        gicr.deleteByGroupContents(groupContentsE); // 이 그룹컨텐츠의 카테고리 전부 삭제 그이후 아래에서 재생성
        for (Integer categoryId : groupContentReqDto.getCategoryIds()) {
            // 1) 카테고리 엔티티 조회
            GroupCategoryList categoryEntity = gcr.findById(categoryId)
                    .orElseThrow(() -> new IllegalStateException("존재하지 않는 카테고리입니다."));

            GroupInCategory groupInCategory = new GroupInCategory();
            groupInCategory.setCategoryId(categoryEntity);
            groupInCategory.setGroupContents(groupContentsE);
            groupContentsE.getCategoryIds().add(groupInCategory); // 그룹컨텐츠에 카테고리들을 조인
        }

        if(groupContentReqDto.getGroupImg() != null && !groupContentReqDto.getGroupImg().isEmpty()) {
            File oldFile = groupContentsE.getFile();
            String filePath = null;
            try {
                filePath = sus.saveFile(groupContentReqDto.getGroupImg());
            } catch (IOException e) {
                log.error("S3 업로드 실패", e);
                throw new IllegalStateException("파일 업로드에 실패했습니다."); // s3에서 에러가났을시 강제 에러실행.
            }
            groupContentsE.getFile().setPath(filePath);
            groupContentsE.getFile().setOriginalname(groupContentReqDto.getGroupImg().getOriginalFilename());
            groupContentsE.getFile().setSize(groupContentReqDto.getGroupImg().getSize());
            groupContentsE.getFile().setContentType(groupContentReqDto.getGroupImg().getContentType());

            // *** db 트랙잭셔널의 롤백현상을 감지하고 시작될 예약 클래스 ( s3 디티오를 스프링에게 알림 에러시 s3rollbacklistener 함수에서 스프링에서 이 디티오를 가져다가 사용함 )
            applicationEventPublisher.publishEvent(new S3DeleteEventDto(groupContentsE.getFile().getOriginalname(), groupContentsE.getFile().getSize(), filePath));


            // 위코드 어디에서든 에러가 난다면 실행되지 않을 것
            try {
                sus.deleteFile(oldFile.getPath());
            } catch (Exception e) {
                S3FileDeleteFailList s3FileDeleteFailList = sfdfm.toS3FileDeleteFailMapper(oldFile, e);
                sfdfr.save(s3FileDeleteFailList);

                throw new IllegalStateException("S3 삭제 실패" + e.getMessage());
            }
        }
    }

    @Transactional
    public void deleteGroup(Long groupId, MemberDto memberdto) {
        GroupContents groupContentE = gconr.findById(groupId).orElseThrow(() -> new IllegalStateException("존재하지 않는 그룹입니다."));
        File oldFile = groupContentE.getFile();
        Member memberE = mr.findById(memberdto.getId()).orElseThrow(() -> new IllegalStateException("존재하지 않는 멤버입니다."));
        GroupMember getGroupMemberInfo = gmr.findByMemberAndGroupContents(memberE, groupContentE).orElseThrow(() -> new IllegalStateException("그룹멤버가 아닙니다."));

        System.out.println(getGroupMemberInfo);
        if(getGroupMemberInfo.getRole() != 1) {
            throw new AccessDeniedException("그룹장이 아닌 그룹원은 삭제 권한이 없습니다.");
        }

        gconr.delete(groupContentE);

        try {
            sus.deleteFile(oldFile.getPath());
        } catch (Exception e) {
            S3FileDeleteFailList s3FileDeleteFailList = sfdfm.toS3FileDeleteFailMapper(oldFile, e);
            sfdfr.save(s3FileDeleteFailList);

            throw new IllegalStateException("S3 삭제 실패" + e.getMessage());
        }
    }

    @Transactional
    public HashMap<String, Object> addViewCount(Long targetId, LocalDate today) {
        GroupContents group = gconr.findById(targetId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 그룹입니다."));

        // 그룹만 오늘 조회수가 있어서 쓰이는 코드
        if (!today.equals(group.getTodayViewDate())) {
            group.setTodayViewDate(today);
            group.setTodayViewCount(0L);
        }

        gconr.increaseViewCount(targetId);
        gconr.increaseTodayViewCount(targetId);  // 그룹만 오늘 조회수가 있어서 쓰이는 코드

        // 조회수 최신화
        entityManager.refresh(group);

        HashMap<String, Object> result = new HashMap<>();
        result.put("allViewCount", group.getAllViewCount());
        result.put("todayViewCount", group.getTodayViewCount()); // 그룹만 오늘 조회수가 있어서 쓰이는 코드

        return result;
    }

    @Transactional(readOnly = true)
    public HashMap<String, Object> getViewCounts(Long targetId) {
        GroupContents group = gconr.findById(targetId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 그룹입니다."));

        HashMap<String, Object> result = new HashMap<>();
        result.put("allViewCount", group.getAllViewCount());
        result.put("todayViewCount", group.getTodayViewCount()); // 그룹만 오늘 조회수가 있어서 쓰이는 코드

        return result;
    }
}
