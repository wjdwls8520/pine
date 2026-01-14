package com.site.pine.service;

import com.site.pine.dto.FileDto;
import com.site.pine.dto.S3DeleteEventDto;
import com.site.pine.dto.community.*;
import com.site.pine.dto.group.GroupPostDetailDto;
import com.site.pine.dto.group.GroupPostDetailResDto;
import com.site.pine.dto.group.GroupPostListDto;
import com.site.pine.dto.member.MemberDto;
import com.site.pine.dto.post.PostMainFileDto;
import com.site.pine.dto.tag.TagResDto;
import com.site.pine.entity.File;
import com.site.pine.entity.Member;
import com.site.pine.entity.Tag;
import com.site.pine.entity.TagMapping;
import com.site.pine.entity.community.CommunityPost;
import com.site.pine.entity.group.GroupPost;
import com.site.pine.entity.post.Post;
import com.site.pine.repository.*;
import com.site.pine.repository.community.CommunityPostRepository;
import com.site.pine.repository.group.GroupContentsRepository;
import com.site.pine.repository.group.GroupPostRepository;
import com.site.pine.repository.like.PostLikeRepository;
import com.site.pine.repository.like.ReplyLikeRepository;
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
import java.util.*;
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
    private final CommunityPostRepository communityPostRepository;
    private final ReplyRepository replyRepository;
    private final ReplyLikeRepository replyLikeRepository;


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

        // 그룹포스트 증가
        groupContentsRepository.increasePostCount(groupId);

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

    @Transactional
    public GroupPostDetailResDto getDetail(Long memberId, Long id) {
        GroupPost groupPost = groupPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다. id=" + id));
        Post post = groupPost.getPost();

        //좋아요여부확인
        boolean isLiked = false;
        if(memberId != null) {
            isLiked = postLikeRepository.existsByPost_IdAndMember_Id(post.getId(), memberId);
        }

        // 엔티티 → DTO 변환
        GroupPostDetailResDto dto = new GroupPostDetailResDto();
        dto.setId(post.getId());
        dto.setContent(post.getContent());
        dto.setLikeCount(post.getLikeCount());
        dto.setReplyCount(post.getReplyCount());
        dto.setStatus(post.getStatus());
        dto.setGroupId(groupPost.getGroupContents().getId());
        dto.setWriteDate(post.getWriteDate());
        dto.setUpdateDate(post.getUpdateDate());
        dto.setLiked(isLiked);

        dto.setNickname(post.getMember().getNickname());
        dto.setProfile_img(post.getMember().getProfile_img());

        // 파일 DTO 리스트 만들 준비
        List<FileDto> postFilesResult = new ArrayList<>();

        // 게시글 엔티티 안에 있는 파일 리스트 꺼내오기
        List<File> postFiles = post.getFiles();

        // 파일 개수만큼 반복
        for (File postFile : postFiles) {
            // FileDto 객체 생성
            FileDto fileDto = new FileDto();
            // File 엔티티 → FileDto 로 값 복사
            fileDto.setId(postFile.getId());
            fileDto.setOriginalname(postFile.getOriginalname());
            fileDto.setSize(postFile.getSize());
            fileDto.setPath(postFile.getPath());
            fileDto.setContentType(postFile.getContentType());
            // 리스트에 추가
            postFilesResult.add(fileDto);
        }
        // DTO에 파일 리스트 넣기
        dto.setFiles(postFilesResult);

        //태그
        List<TagResDto> tags = tagService.getTagsByPostIds(List.of(post.getId()));
        dto.setTags(tags);

        dto.setMemberId(post.getMember().getId());

        return dto;
    }

    // 수정 페이지 진입 시 기존 데이터 조회
    @Transactional(readOnly = true)
    public GroupPostDetailDto getPostDetail(Long postId) {
        // Fetch Join으로 Post까지 한 번에 조회
        GroupPost cp = groupPostRepository.findByIdWithPost(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        //태그가져오기
        List<String> tags = tagService.getTags(postId);

        return new GroupPostDetailDto(cp, tags);
    }


    @Transactional
    public void modifyPost(Long postId, PostModifyDto dto, Long memberId) {

        // 1. 게시글 조회 (CommunityPost + Post + Member 까지 페치 조인 추천)
        GroupPost groupPost = groupPostRepository.findByIdWithPost(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        Post post = groupPost.getPost();

        // 2. 권한 체크 (내 글인지?)
        if (!post.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다."); // Controller에서 403 처리됨
        }

        // ==================================================
        // 3. 기본 정보 수정 (Dirty Checking)
        // ==================================================
        post.setContent(dto.getContent());       // 본문 수정
        post.setStatus(dto.getStatus());         // 공개/비공개 수정

        // ==================================================
        // 4. 태그 수정
        // ==================================================
        if (dto.getTags() != null) {
            tagService.updateTags(postId, dto.getTags());
        }

        // 5. 파일 삭제 (사용자가 삭제 버튼 누른 파일들)
        if (dto.getDeleteFileIds() != null && !dto.getDeleteFileIds().isEmpty()) {
            // 1. DB에서 파일 정보 조회
            List<File> deleteFiles = fileRepository.findAllById(dto.getDeleteFileIds());

            // 2. S3에서 실제 파일 삭제
            for (File file : deleteFiles) {
                sus.deleteFile(file.getPath());
            }

            // 3. DB에서 삭제
            fileRepository.deleteAll(deleteFiles);
        }

        // =========================================================
        // 6.새 파일 업로드 (insertPost 로직 재사용)
        // =========================================================
        if (dto.getNewFiles() != null && !dto.getNewFiles().isEmpty()) {

            for (MultipartFile file : dto.getNewFiles()) {
                if (file.isEmpty()) continue;

                String fileUrl;
                try {
                    fileUrl = sus.saveFile(file); // S3 업로드
                } catch (IOException e) {
                    log.error("S3 업로드 실패", e);
                    throw new RuntimeException("파일 업로드 실패");
                }

                // S3 롤백 이벤트 발행 (필요시)
                applicationEventPublisher.publishEvent(new S3DeleteEventDto(file.getOriginalFilename(), file.getSize(), fileUrl));

                // 파일 엔티티 저장
                File fileEntity = new File();
                fileEntity.setPath(fileUrl);
                fileEntity.setOriginalname(file.getOriginalFilename());
                fileEntity.setContentType(file.getContentType());
                fileEntity.setSize(file.getSize());
                fileEntity.setStatus(2);
                fileEntity.setPost(post); // 현재 게시글에 연결

                fileRepository.save(fileEntity);
            }
        }

        // 트랜잭션 종료 시 update 쿼리가 자동으로 날아감
    }

    @Transactional
    public void deletePost(Long postId, Long memberId) {
        // 1. 게시글 조회 (없으면 에러)
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 2. 주인 확인 (내 글 아니면 에러)
        if (!post.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        // ==========================================
        // 3. 연관 데이터 삭제 (청소 시작!) 🧹
        // ==========================================

        // 그룹포스트 감소
        GroupPost gp = groupPostRepository.findByPost(post);
        if(gp.getGroupContents().getPostCount() > 0) {
            groupContentsRepository.decreasePostCount(gp.getGroupContents().getId());
        }

        // 3-1. 태그 매핑 삭제
        tagService.deleteTags(postId);

        // 3-2. 게시글 좋아요 삭제
        postLikeRepository.deleteByPost(post);

        // (1) 댓글 좋아요 삭제
        replyLikeRepository.deleteAllByPost(post);

        // (2) 대댓글(자식) 먼저 삭제 🧹
        replyRepository.deleteChildRepliesByPostId(postId);

        // (3) 메인댓글(부모) 나중에 삭제 🧹
        replyRepository.deleteParentRepliesByPostId(postId);

        // S3 삭제가 끝난 후 DB 데이터 삭제
        fileRepository.deleteByPost(post);

        // 3-4. CommunityPost(카테고리 연결) 삭제
        groupPostRepository.deleteByPostId(postId);

        // 4. 게시글 삭제
        postRepository.delete(post);


        // 파일(S3 + DB) 삭제
        // post.getFiles() 대신 리포지토리에서 직접 조회 (LazyInitializationException 방지)
        List<File> files = fileRepository.findAllByPost(post);

        if (files != null && !files.isEmpty()) {
            for (File file : files) {
                try {
                    sus.deleteFile(file.getPath()); // S3 삭제
                } catch (Exception e) {
                    log.error("S3 파일 삭제 실패: {}", file.getPath());
                }
            }
        }

    }

    public Page<GroupPostListDto> getGroupPost(Long groupId) { // 로그인 유저 ID 추가
        Pageable pageable = PageRequest.of(0, 5);

        // 1. 엔티티 조회 (Fetch Join으로 이미 연관 데이터 다 가져옴)
        Page<GroupPost> groupPosts = groupPostRepository.findByGroupPost(groupId, pageable);

        // 2. 엔티티 -> DTO 변환 (map 함수 사용)
        Page<GroupPostListDto> dtoPage = groupPosts.map(gp -> {
            Post post = gp.getPost();
            Member member = post.getMember();

            GroupPostListDto dto = new GroupPostListDto(
                    post.getId(),          // postId
                    post.getContent(),     // content
                    gp.getGroupContents().getId(), // groupId
                    post.getLikeCount(),
                    post.getReplyCount(),
                    post.getWriteDate(),
                    member.getId(),
                    member.getNickname(),
                    member.getProfile_img()
            );

            return dto;
        });

        return dtoPage; // List보다는 Page를 그대로 리턴하는 게 프론트에서 '다음 페이지' 처리하기 좋습니다.
    }
}
