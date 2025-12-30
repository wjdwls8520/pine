package com.site.pine.service;

import com.site.pine.dto.FileDto;
import com.site.pine.dto.S3DeleteEventDto;
import com.site.pine.dto.community.PostMainFileDto;
import com.site.pine.dto.community.PostMainListDto;
import com.site.pine.dto.community.PostCreateReqDto;
import com.site.pine.dto.community.PostDetailResDto;
import com.site.pine.dto.member.MemberDto;
import com.site.pine.dto.tag.TagResDto;
import com.site.pine.entity.*;
import com.site.pine.entity.community.CommunityPost;
import com.site.pine.entity.post.Post;
import com.site.pine.enums.PageType;
import com.site.pine.repository.*;
import com.site.pine.repository.community.CommunityPostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CommunityService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final CommunityPostRepository cpr;
    private final PostRepository cr;
    private final S3UploadService sus;
    private final FileRepository fr;
    private final LikesRepository lr;
    private final MemberRepository mr;
    private final TagRepository tr;
    private final TagMappingRepository tmr;

    public void insertPost(MemberDto mdto, PostCreateReqDto reqDto) {

        Member memberEntity = mr.findById(mdto.getId()).orElseThrow(() -> new IllegalStateException("[error] 존재하지 않는 멤버 입니다.")); // 멤버조회 대상이 없을시 강제 에러실행.;

        //post저장
        Post postEntity = new Post();
        postEntity.setContent(reqDto.getPostBody());
        postEntity.setStatus(reqDto.getStatus());
        postEntity.setMember(memberEntity);
        cr.save(postEntity);

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
                mapping.setTargetType(PageType.COMMUNITY); // POST
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
            applicationEventPublisher.publishEvent(new S3DeleteEventDto(PageType.COMMUNITY, file.getOriginalFilename(), file.getSize(), fileUrl));

            File fileEntity = new File();

            // s3저장
            fileEntity.setPath(fileUrl);

            // 기본파일객체 정보 저장
            fileEntity.setOriginalname(file.getOriginalFilename());
            fileEntity.setContentType(file.getContentType());
            fileEntity.setSize(file.getSize());
            fileEntity.setPageType(PageType.COMMUNITY);

            // 포스트 조인
            fileEntity.setPost(postEntity);

            fr.save(fileEntity);
        }

        CommunityPost communityPost = new CommunityPost();
        communityPost.setPost(postEntity);
        communityPost.setCategory(reqDto.getCategory());
        cpr.save(communityPost);

    }

    public HashMap<String, Object> getPostPage(MemberDto mdto, Integer page) {
        HashMap<String, Object> result = new HashMap<>();

        Pageable pageable = PageRequest.of(page, 6);

        Page<PostMainListDto> postPages = cr.getAllCommunityPostList(pageable);
        System.out.println();
        List<PostMainListDto> posts = postPages.getContent();


        // 1️⃣ 게시글 ID 리스트 추출
        List<Long> postIds = posts.stream().map(PostMainListDto::getPostId).collect(Collectors.toList());

        // 2️⃣ 파일 조회
        List<PostMainFileDto> files = fr.findFilesByPostIds(postIds);

        // 3️⃣ DTO에 파일 주입
        Map<Long, List<PostMainFileDto>> fileMap = files.stream()
                .collect(Collectors.groupingBy(PostMainFileDto::getPostId));

        for (PostMainListDto post : posts) {
            if (fileMap.containsKey(post.getPostId())) {
                fileMap.get(post.getPostId()).forEach(post::addFile);
            }
        }



        // 태그 조회
        List<TagResDto> tags =
                tmr.findTagsByTargetIds(PageType.COMMUNITY, postIds);

        Map<Long, List<TagResDto>> tagMap = tags.stream()
                .collect(Collectors.groupingBy(TagResDto::getTargetId));

        for (PostMainListDto post : posts) {
            if (tagMap.containsKey(post.getPostId())) {
                tagMap.get(post.getPostId()).forEach(post::addTag);
            }
        }

        System.out.println("===== TAG DEBUG =====");
        System.out.println("posts size = " + posts.size());
        System.out.println("postIds = " + postIds);
        System.out.println("tags size = " + tags.size());


        // 4️⃣ 로그인 유저가 좋아요 눌렀는지 체크
        if (mdto != null) { // 로그인 상태일 때만
            for (PostMainListDto post : posts) {
                boolean liked = lr.existsByMember_IdAndTargetTypeAndTargetId(
                        mdto.getId(), PageType.COMMUNITY, post.getPostId()
                );
                post.setLiked(liked);
            }
        }

        result.put("postList", posts);
        result.put("totalPage", postPages.getTotalPages());

        return result;
    }


    public PostDetailResDto getDetail(Long memberId, Long id) {
        CommunityPost communityPost = cpr.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다. id=" + id));
        Post post = communityPost.getPost();

        //좋아요여부확인
        boolean isLiked = false;
        if(memberId != null) {
            isLiked  = lr.existsByMember_IdAndTargetTypeAndTargetId(memberId, PageType.COMMUNITY, post.getId());
        }

        // 엔티티 → DTO 변환
        PostDetailResDto dto = new PostDetailResDto();
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
            fileDto.setPageType(postFile.getPageType());
            fileDto.setOriginalname(postFile.getOriginalname());
            fileDto.setSize(postFile.getSize());
            fileDto.setPath(postFile.getPath());
            fileDto.setContentType(postFile.getContentType());
            // 리스트에 추가
            postFilesResult.add(fileDto);
        }
        // DTO에 파일 리스트 넣기
        dto.setFiles(postFilesResult);

        return dto;
    }


    public int toggleLike(Long postId, Long memberId) {
        Optional<Likes> existingLike = lr.findByMember_IdAndTargetTypeAndTargetId(memberId, PageType.COMMUNITY, postId);

        Post post = cr.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("포스트가 존재하지 않습니다."));

        if (existingLike.isPresent()) {
            // 좋아요 취소
            lr.delete(existingLike.get());
            post.setLikeCount(post.getLikeCount() - 1);
        } else {
            // 좋아요 추가
            Likes like = new Likes();
            like.setTargetType(PageType.COMMUNITY); // POST_TYPE
            like.setTargetId(postId);
            like.setMember(new Member());
            like.getMember().setId(memberId);
            lr.save(like);
            post.setLikeCount(post.getLikeCount() + 1);
        }

        cr.save(post); // 변경된 likeCount 저장
        return post.getLikeCount();
    }
}
