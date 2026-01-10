package com.site.pine.service;

import com.site.pine.dto.S3DeleteEventDto;
import com.site.pine.dto.community.CommunityCreateReqDto;
import com.site.pine.dto.community.CommunityListDto;
import com.site.pine.dto.group.GroupPostListDto;
import com.site.pine.dto.member.MemberDto;
import com.site.pine.dto.post.PostMainFileDto;
import com.site.pine.dto.tag.TagResDto;
import com.site.pine.entity.File;
import com.site.pine.entity.Member;
import com.site.pine.entity.Tag;
import com.site.pine.entity.TagMapping;
import com.site.pine.entity.group.GroupPost;
import com.site.pine.entity.post.Post;
import com.site.pine.repository.*;
import com.site.pine.repository.group.GroupContentsRepository;
import com.site.pine.repository.group.GroupPostRepository;
import com.site.pine.repository.like.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupPostService {

    private final ApplicationEventPublisher applicationEventPublisher;

    private final GroupAuthorizationService groupAuthorizationService;
    private final TagService tagService;

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final TagMappingRepository tagMappingRepository;
    private final S3UploadService sus;
    private final FileRepository fileRepository;
    private final PostLikeRepository postLikeRepository;
    private final GroupPostRepository groupPostRepository;
    private final GroupContentsRepository groupContentsRepository;


    @Transactional
    public void insertPost(MemberDto mdto, Long groupId, CommunityCreateReqDto reqDto) {

        Member memberEntity = memberRepository.findById(mdto.getId()).orElseThrow(() -> new IllegalStateException("[error] 존재하지 않는 멤버 입니다.")); // 멤버조회 대상이 없을시 강제 에러실행.;

        //post저장
        Post postEntity = new Post();
        postEntity.setContent(reqDto.getPostBody());
        postEntity.setStatus(reqDto.getStatus());
        postEntity.setMember(memberEntity);
        postRepository.save(postEntity);

        //  태그 처리
        tagService.updateTags(postEntity.getId(), reqDto.getTags());

        // 파일테이블 저장 및 s3업로드
        List<MultipartFile> fileList = reqDto.getFiles();
        for(MultipartFile file : fileList) {
            if (file == null || file.isEmpty()) continue;
            String fileUrl;
            try {
                fileUrl = sus.saveFile(file); // S3 업로드
            } catch (IOException e) {
                log.error("S3 업로드 실패", e);
                throw new IllegalStateException("파일 업로드에 실패했습니다."); // s3에서 에러가났을시 강제 에러실행.
            }

            // db 트랙잭셔널의 롤백현상을 감지하고 시작될 예약 클래스 ( s3 디티오를 스프링에게 알림 에러시 s3rollbacklistener 함수에서 스프링에서 이 디티오를 가져다가 사용함 )
            applicationEventPublisher.publishEvent(new S3DeleteEventDto(file.getOriginalFilename(), file.getSize(), fileUrl));

            File fileEntity = new File();

            // s3저장
            fileEntity.setPath(fileUrl);

            // 기본파일객체 정보 저장
            fileEntity.setOriginalname(file.getOriginalFilename());
            fileEntity.setContentType(file.getContentType());
            fileEntity.setSize(file.getSize());

            // 업로드 성공했으니 상태를 '2(완료)'로 설정!
            fileEntity.setStatus(2);

            // 포스트 조인
            fileEntity.setPost(postEntity);

            fileRepository.save(fileEntity);
        }


        GroupPost groupPost = new GroupPost();
        groupPost.setPost(postEntity);
        groupPost.setGroupContents(groupAuthorizationService.getGroupOrThrow(groupId));
        groupPostRepository.save(groupPost);

    }



    @Transactional(readOnly = true)
    public HashMap<String, Object> getPostPage(MemberDto memberDto, Integer page, Long groupId) {
        HashMap<String, Object> result = new HashMap<>();

        Pageable pageable = PageRequest.of(page, 6);

        Page<GroupPostListDto> postPages = postRepository.getAllGroupPostList(pageable, groupId);
        System.out.println();
        List<GroupPostListDto> posts = postPages.getContent();


        // 1️⃣ 게시글 ID 리스트 추출
        List<Long> postIds = posts.stream().map(GroupPostListDto::getPostId).collect(Collectors.toList());

        // 2️⃣ 파일 조회
        List<PostMainFileDto> files = fileRepository.findFilesByPostIds(postIds);

        // 3️⃣ DTO에 파일 주입
        Map<Long, List<PostMainFileDto>> fileMap = files.stream()
                .collect(Collectors.groupingBy(PostMainFileDto::getPostId));

        for (GroupPostListDto post : posts) {
            if (fileMap.containsKey(post.getPostId())) {
                fileMap.get(post.getPostId()).forEach(post::addFile);
            }
        }



        // 태그 조회
        List<TagResDto> tags = tagService.getTagsByPostIds(postIds);

        Map<Long, List<TagResDto>> tagMap = tags.stream()
                .collect(Collectors.groupingBy(TagResDto::getTargetId));

        for (GroupPostListDto post : posts) {
            if (tagMap.containsKey(post.getPostId())) {
                tagMap.get(post.getPostId()).forEach(post::addTag);
            }
        }

        System.out.println("===== TAG DEBUG =====");
        System.out.println("posts size = " + posts.size());
        System.out.println("postIds = " + postIds);
        System.out.println("tags size = " + tags.size());


        // 좋아요 여부 , 내글 체크
        if (memberDto != null) { // 로그인 상태일 때만

            Long currentUserId = memberDto.getId(); // 현재 로그인한 사람 ID

            for (GroupPostListDto post : posts) {
                //좋아요 체크
                boolean liked = postLikeRepository.existsByPost_IdAndMember_Id(post.getPostId(), memberDto.getId());
                post.setLiked(liked);

                // 🔥 2. [추가] 내 글인지 체크 (Owner)
                // 작성자ID와 로그인한ID가 같으면 true
                if (post.getMemberId().equals(currentUserId)) {
                    post.setOwner(true);
                } else {
                    post.setOwner(false);
                }
            }
        }

        result.put("postList", posts);
        result.put("totalPage", postPages.getTotalPages());

        return result;
    }
}
