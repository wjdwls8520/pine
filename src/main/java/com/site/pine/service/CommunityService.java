package com.site.pine.service;

import com.site.pine.dto.FileDto;
import com.site.pine.dto.S3DeleteEventDto;
import com.site.pine.dto.community.*;
import com.site.pine.dto.post.PostMainFileDto;
import com.site.pine.dto.member.MemberDto;
import com.site.pine.dto.tag.TagResDto;
import com.site.pine.entity.*;
import com.site.pine.entity.community.CommunityPost;
import com.site.pine.entity.post.Post;
import com.site.pine.repository.*;
import com.site.pine.repository.community.CommunityPostRepository;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommunityService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final CommunityPostRepository cpr;
    private final PostRepository pr;
    private final S3UploadService sus;
    private final FileRepository fr;
    private final PostLikeRepository lr;
    private final MemberRepository mr;
    private final TagRepository tr;
    private final TagMappingRepository tmr;

    @Transactional
    public void insertPost(MemberDto mdto, CommunityCreateReqDto reqDto) {

        Member memberEntity = mr.findById(mdto.getId()).orElseThrow(() -> new IllegalStateException("[error] 존재하지 않는 멤버 입니다.")); // 멤버조회 대상이 없을시 강제 에러실행.;

        //post저장
        Post postEntity = new Post();
        postEntity.setContent(reqDto.getPostBody());
        postEntity.setStatus(reqDto.getStatus());
        postEntity.setMember(memberEntity);
        pr.save(postEntity);

        //  태그 처리
        if (reqDto.getTags() != null && !reqDto.getTags().isBlank()) {

            List<String> tagNames = Arrays.stream(reqDto.getTags().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .distinct()
                    .toList();

            for (String tagName : tagNames) {

                Tag tag = tr.findByName(tagName)
                        .orElseGet(() -> tr.save(new Tag(null, tagName)));

                TagMapping mapping = new TagMapping();
                mapping.setTag(tag);
                mapping.setTargetId(postEntity.getId());

                tmr.save(mapping);
            }
        }

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

            fr.save(fileEntity);
        }

        CommunityPost communityPost = new CommunityPost();
        communityPost.setPost(postEntity);
        communityPost.setCategory(reqDto.getCategory());
        cpr.save(communityPost);

    }

    @Transactional(readOnly = true)
    public HashMap<String, Object> getPostPage(MemberDto mdto, Integer page) {
        HashMap<String, Object> result = new HashMap<>();

        Pageable pageable = PageRequest.of(page, 6);

        Page<CommunityListDto> postPages = pr.getAllCommunityPostList(pageable);
        System.out.println();
        List<CommunityListDto> posts = postPages.getContent();


        // 1️⃣ 게시글 ID 리스트 추출
        List<Long> postIds = posts.stream().map(CommunityListDto::getPostId).collect(Collectors.toList());

        // 2️⃣ 파일 조회
        List<PostMainFileDto> files = fr.findFilesByPostIds(postIds);

        // 3️⃣ DTO에 파일 주입
        Map<Long, List<PostMainFileDto>> fileMap = files.stream()
                .collect(Collectors.groupingBy(PostMainFileDto::getPostId));

        for (CommunityListDto post : posts) {
            if (fileMap.containsKey(post.getPostId())) {
                fileMap.get(post.getPostId()).forEach(post::addFile);
            }
        }



        // 태그 조회
        List<TagResDto> tags =
                tmr.findTagsByTargetIds(postIds);

        Map<Long, List<TagResDto>> tagMap = tags.stream()
                .collect(Collectors.groupingBy(TagResDto::getTargetId));

        for (CommunityListDto post : posts) {
            if (tagMap.containsKey(post.getPostId())) {
                tagMap.get(post.getPostId()).forEach(post::addTag);
            }
        }

        System.out.println("===== TAG DEBUG =====");
        System.out.println("posts size = " + posts.size());
        System.out.println("postIds = " + postIds);
        System.out.println("tags size = " + tags.size());


        // 좋아요 여부 , 내글 체크
        if (mdto != null) { // 로그인 상태일 때만

            Long currentUserId = mdto.getId(); // 현재 로그인한 사람 ID

            for (CommunityListDto post : posts) {
                //좋아요 체크
                boolean liked = lr.existsByPost_IdAndMember_Id(post.getPostId(), mdto.getId());
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
    public CommunityDetailResDto getDetail(Long memberId, Long id) {
        CommunityPost communityPost = cpr.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다. id=" + id));
        Post post = communityPost.getPost();

        //좋아요여부확인
        boolean isLiked = false;
        if(memberId != null) {
            isLiked = lr.existsByPost_IdAndMember_Id(post.getId(), memberId);
        }

        // 엔티티 → DTO 변환
        CommunityDetailResDto dto = new CommunityDetailResDto();
        dto.setId(post.getId());
        dto.setContent(post.getContent());
        dto.setLikeCount(post.getLikeCount());
        dto.setReplyCount(post.getReplyCount());
        dto.setStatus(post.getStatus());
        dto.setCategory(communityPost.getCategory());
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
        List<TagResDto> tags = tmr.findTagsByTargetIds(List.of(post.getId()));
        // DTO에 넣기
        dto.setTags(tags);

        dto.setMemberId(post.getMember().getId());

        return dto;
    }

    @Transactional
    public void deletePost(Long postId, Long memberId) {
        // 1. 게시글 조회 (없으면 에러)

        Post post = pr.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 2. 주인 확인 (내 글 아니면 에러)
        if (!post.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        // ==========================================
        // 3. 연관 데이터 삭제 (청소 시작!) 🧹
        // ==========================================

        // 3-1. 태그 매핑 삭제
        tmr.deleteByTargetId(postId);

        // 3-2. 좋아요 삭제
        lr.deleteByPost(post);

        // 3-3. 파일(이미지) DB 데이터 삭제
        // (실제 S3 파일 삭제는 나중에 구현해도 됩니다. 일단 DB부터!)
        fr.deleteByPost(post);

        // 3-4. 댓글 삭제 (ReplyRepository가 있다면)
        // replyRepository.deleteByPost(post);

        // 3-5. CommunityPost(카테고리 연결) 삭제
        cpr.deleteByPost(post);


        // ==========================================
        // 4. 대망의 게시글 삭제 💣
        // ==========================================
        pr.delete(post);
    }


    // 수정 페이지 진입 시 기존 데이터 조회
    @Transactional(readOnly = true)
    public PostDetailDto getPostDetail(Long postId) {
        // Fetch Join으로 Post까지 한 번에 조회
        CommunityPost cp = cpr.findByIdWithPost(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 태그 조회 (기존에 만들어둔 메서드 활용 or 새로 조회)
        // 여기선 간단히 TagMappingRepository에서 이름만 가져온다고 가정
        List<String> tags = tmr.findTagsByTargetIds(List.of(postId))
                .stream().map(TagResDto::getName).toList();

        return new PostDetailDto(cp, tags);
    }

    // 게시글 수정
    @Transactional
    public void modifyPost(Long postId, PostModifyDto dto, Long memberId) {

        // 1. 게시글 조회 (CommunityPost + Post + Member 까지 페치 조인 추천)
        CommunityPost communityPost = cpr.findByIdWithPost(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        Post post = communityPost.getPost();

        // 2. 권한 체크 (내 글인지?)
        if (!post.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다."); // Controller에서 403 처리됨
        }

        // ==================================================
        // 3. 기본 정보 수정 (Dirty Checking)
        // ==================================================
        post.setContent(dto.getContent());       // 본문 수정
        post.setStatus(dto.getStatus());         // 공개/비공개 수정
        communityPost.setCategory(dto.getCategory()); // 카테고리 수정

        // ==================================================
        // 4. 태그 수정
        // ==================================================
        // 기존 태그 매핑을 싹 지우고, 새로 입력된 태그를 다시 등록하는 방식이 제일 깔끔함
        if (dto.getTags() != null) {
            // 4-1. 기존 매핑 삭제
            tmr.deleteByTargetId(postId);

            // 삭제 쿼리를 DB에 즉시 반영 (강제 플러시)
            // 이걸 안 하면 아래 save() 할 때 "이미 데이터가 있다"며 에러가 남
            tmr.flush();

            // 4-2. 새 태그 등록 (insertPost 로직 재사용)
            // 빈 값이 아닐 때만 실행
            if (!dto.getTags().isBlank()) {
                List<String> tagNames = Arrays.stream(dto.getTags().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .distinct()
                        .toList();

                for (String tagName : tagNames) {
                    // 태그 테이블에 없으면 저장, 있으면 가져오기
                    Tag tag = tr.findByName(tagName)
                            .orElseGet(() -> tr.save(new Tag(null, tagName)));

                    // 매핑 테이블 저장
                    TagMapping mapping = new TagMapping();
                    mapping.setTag(tag);
                    mapping.setTargetId(postId);
                    tmr.save(mapping);
                }
            }
        }

        // ==================================================
        // 5. 파일 삭제 (사용자가 삭제 버튼 누른 파일들)
        // ==================================================
        if (dto.getDeleteFileIds() != null && !dto.getDeleteFileIds().isEmpty()) {
            // DB에서 해당 파일들 삭제
            fr.deleteAllById(dto.getDeleteFileIds()); //이거쓰면안됨 수정필요!

            // S3에서도 실제 파일을 지우려면 여기서 S3UploadService 호출
            // for (Long fileId : dto.getDeleteFileIds()) { ... }
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

                fr.save(fileEntity);
            }
        }

        // 트랜잭션 종료 시 update 쿼리가 자동으로 날아감
    }
}
