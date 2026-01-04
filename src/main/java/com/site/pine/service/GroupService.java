package com.site.pine.service;

import com.site.pine.dto.S3DeleteEventDto;
import com.site.pine.dto.group.*;
import com.site.pine.dto.member.MemberDto;
import com.site.pine.entity.File;
import com.site.pine.entity.Member;
import com.site.pine.entity.S3FileDeleteFailList;
import com.site.pine.entity.ViewGroupHistory;
import com.site.pine.entity.group.*;
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
import org.springframework.data.domain.Sort;
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
    private final ViewGroupRepository gvr;

    private final MemberRepository mr;
    private final GroupMemberRepository gmr;
    private final GroupJoinRequestRepository gjrr;

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
        groupContentsE.setGroupMemberCount(0L);


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

        // 그룹엔티티와 연결된 파일엔티티 그리고 그룹멤버를 같이 저장함
        gconr.save(groupContentsE);


        // 그룹멤버엔티티에 멤버엔티티와 그룹엔티티를 추가해서 그룹 -> 그룹멤버 <- 멤버 그룹멤버라는 중간연결엔티티를 제작
        GroupMember groupMemberEntity = new GroupMember();
        groupMemberEntity.setGroupContents(groupContentsE);
        groupMemberEntity.setMember(memberEntity);
        groupMemberEntity.setRole(1); // 그룹장
        groupMemberEntity.setGroupContents(groupContentsE);
        gmr.save(groupMemberEntity);

        // 그룹멤버수 1추가 (그룹장)
        groupContentsE.setGroupMemberCount(groupContentsE.getGroupMemberCount() + 1);


        // 내가 클라이언트한테 받은 선택된 카테고리 개수만큼 for 반복
        List<GroupInCategory> categoryList = new ArrayList<>();
        for (Integer categoryId : groupContentReqDto.getCategoryIds()) {
            // 1) 카테고리 엔티티 조회
            GroupCategoryList categoryEntity = gcr.findById(categoryId)
                    .orElseThrow(() -> new IllegalStateException("존재하지 않는 카테고리입니다."));

            GroupInCategory groupInCategory = new GroupInCategory();
            groupInCategory.setCategoryId(categoryEntity);
            groupInCategory.setGroupContents(groupContentsE);

            categoryList.add(groupInCategory);
        }
        gicr.saveAll(categoryList);

    }


    // 하나의 그룹 디테일
    @Transactional(readOnly = true)
    public GroupContentResDto getGroup(Long id) {

        GroupContents groupDetailE = gconr.findById(id).orElseThrow(()-> new IllegalStateException("존재하지 않는 그룹입니다."));

        GroupContentResDto groupContentResDto = gm.toGroupContentResDto(groupDetailE);

        List<GroupCategoryDto> categoryResult = new ArrayList<>();
        List<GroupInCategory> getAllGIC = gicr.findAllByGroupIdWithCategory(id);
        for (GroupInCategory category : getAllGIC) {
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


        gicr.deleteAllByGroupContents(groupContentsE); // 이 그룹컨텐츠의 카테고리 전부 삭제 그이후 아래에서 재생성
        List<GroupInCategory> categoryList = new ArrayList<>();
        for (Integer categoryId : groupContentReqDto.getCategoryIds()) {
            // 1) 카테고리 엔티티 조회
            GroupCategoryList categoryEntity = gcr.findById(categoryId)
                    .orElseThrow(() -> new IllegalStateException("존재하지 않는 카테고리입니다."));

            GroupInCategory groupInCategory = new GroupInCategory();
            groupInCategory.setCategoryId(categoryEntity);
            groupInCategory.setGroupContents(groupContentsE);
            categoryList.add(groupInCategory);
        }
        gicr.saveAll(categoryList);

        if(groupContentReqDto.getGroupImg() != null && !groupContentReqDto.getGroupImg().isEmpty()) {
            S3DeleteEventDto oldFile = new S3DeleteEventDto(groupContentsE.getFile().getOriginalname(), groupContentsE.getFile().getSize(), groupContentsE.getFile().getPath());
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

                log.warn("기존 파일 삭제 실패 (추후 배치 삭제 요망): {}", oldFile.getPath(), e);
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

        entityManager.detach(getGroupMemberInfo);

        gicr.deleteAllByGroupContents(groupContentE); // 그룹 카테고리 삭제
        gvr.deleteAllByGroupId(groupId); // 그룹조회수히스토리 삭제 ( Modifying )
        gjrr.deleteAllByGroupId(groupId); // 그룹신청리스트 삭제 ( Modifying )
        gmr.deleteAllByGroupId(groupId); // 그룹멤버 삭제 ( Modifying )
        gconr.delete(groupContentE); // 그룹 하나 삭제

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

    @Transactional
    public String insertJoinGroupMember(MemberDto memberdto, GroupJoinRequestDto reqdto) {

        Member memberE = mr.findById(memberdto.getId())
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 멤버입니다."));

        GroupContents groupE = gconr.findById(reqdto.getGroupId())
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 그룹입니다."));

        Boolean isGroupMember = gmr.existsByGroupContentsAndMember(groupE, memberE);

        Boolean isGroupJoinRequest = gjrr.existsByGroupContentsAndMemberAndStatus(groupE, memberE, 0);

        // 가입 신청 불가능 옵션일 때
        if(groupE.getJoinState() == 0) {
            throw new IllegalStateException("[error] 해당 그룹은 가입신청이 불가능한 상태의 그룹입니다."); // 그룹 설정이 가입 불가능 이라면
        }

        // 가입 신청 가능 옵션일 때
        if(isGroupMember) throw new IllegalStateException("[error] 이미 해당 그룹에 가입되어 있으십니다.");  // 이미 그룹멤버라면 불가능.
        if(isGroupJoinRequest) throw new IllegalStateException("[error] 이미 해당 그룹에 가입신청이 되어 있으십니다.");  // 이미 그룹신청멤버라면 불가능.


        if (groupE.getAutoJoin() == 1) {
            // [자동 승인] 즉시 그룹 멤버로 추가
            GroupMember newMember = new GroupMember();
            newMember.setGroupContents(groupE);
            newMember.setMember(memberE);
            newMember.setRole(3); // 일반 회원
            gmr.save(newMember);

            // 멤버 수 증가 및 조회수 갱신 등
            gconr.increaseGroupMemberCount(groupE.getId());

            // 그룹신청요청리스트에는 반영안됨
            return "그룹 가입이 완료되었습니다.";
        } else {
            // [관리자 승인] 그룹신청리스트(대기)로 추가
            GroupJoinRequest request = GroupJoinRequest.builder()
                .groupContents(groupE)
                .member(memberE)
                .introduction(reqdto.getIntroduction())
                .status(0)
                .build();
            gjrr.save(request);
            return "그룹 가입신청이 완료되었습니다.";
        }
    }

    @Transactional(readOnly = true)
    public Page<GroupJoinResDto> getGroupJoinList(MemberDto memberdto, Long groupId, Integer page) {
        // 1. 배열은 조회값이 없으면 [] 가기 때문에 0을 체크해 줄 필요없음.
        // 2. 프론트엔드에서 length = 0 일 때를 판별하면 끝

        // 요청한사람이 해당그룹의 그룹장인지아닌지 판단
        GroupMember isGroupMember = gmr.findByGroupContentsIdAndMemberId(groupId, memberdto.getId()).orElseThrow(() -> new IllegalArgumentException("잘못된 접근입니다."));
        if(isGroupMember.getRole() != 1) throw new IllegalArgumentException("잘못된 접근입니다.");

        Pageable pageable = PageRequest.of(page, 6, Sort.by(Sort.Direction.DESC, "requestDate"));
        Page<GroupJoinResDto> groupJoinList = gjrr.findAllJoinGroupAndMember(pageable , groupId);
        return groupJoinList;
    }

    @Transactional
    public void gjoinReqAppRej(MemberDto memberdto, GroupJoinAppJejReqDto reqdto) {

        // 요청한사람이 해당그룹의 그룹장인지아닌지 판단
        GroupMember isGroupMember = gmr.findByGroupContentsIdAndMemberId(reqdto.getGroupId(), memberdto.getId()).orElseThrow(() -> new IllegalArgumentException("잘못된 접근입니다."));
        if(isGroupMember.getRole() != 1) throw new IllegalArgumentException("잘못된 접근입니다.");

        // 2. [데이터 검증] 처리하려는 가입 신청서(Request) 조회
        // 단순히 ID로 지우는 게 아니라, 조회해서 검증해야 함
        GroupJoinRequest requestE = gjrr.findById(reqdto.getJoinId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신청서입니다."));

        // 3. [보안 검증 - IDOR 방어] 신청서가 해당 그룹의 것이 맞는지 확인
        if(!requestE.getGroupContents().getId().equals(reqdto.getGroupId())) {
            throw new IllegalArgumentException("잘못된 접근입니다. (그룹 불일치)");
        }

        if(!requestE.getMember().getId().equals(reqdto.getMemberId())) {
            throw new IllegalArgumentException("잘못된 접근입니다. (멤버 불일치)");
        }

        // 4. 승인/거절 로직
        if("APPROVE".equals(reqdto.getStatus())) {

            // [무결성 검증] 이미 가입된 멤버인지 더블 체크 ★★★
            boolean alreadyMember = gmr.existsByGroupContentsIdAndMemberId(reqdto.getGroupId(), requestE.getMember().getId());
            if (alreadyMember) {
                // 이미 멤버라면 신청서만 지우고 종료하거나 에러 처리
                gjrr.delete(requestE);
                return;
            }

            // 멤버 추가
            GroupMember newMember = new GroupMember();
            newMember.setGroupContents(requestE.getGroupContents());
            newMember.setMember(requestE.getMember());
            newMember.setRole(3);
            gmr.save(newMember); // 그룹멤버에 추가

            gconr.increaseGroupMemberCount(reqdto.getGroupId()); // 그룹에 멤버카운트 추가

            gjrr.delete(requestE); // 그룹신청리스트에서 삭제
        } else if("REJECT".equals(reqdto.getStatus())) {
            gjrr.delete(requestE);
        } else {
            throw new IllegalArgumentException("잘못된 상태값입니다.");
        }

    }

    // 가입신청 중인지 확인 api
    public Boolean isGroupJoinRequest(Long memberId, Long groupId) {
        return gjrr.existsByGroupContentsIdAndMemberIdAndStatus(groupId, memberId, 0);
    }
}
