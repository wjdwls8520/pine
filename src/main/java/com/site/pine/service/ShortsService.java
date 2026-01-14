package com.site.pine.service;

import com.site.pine.dto.S3DeleteEventDto;
import com.site.pine.dto.post.PostMainFileDto;
import com.site.pine.dto.member.MemberDto;
import com.site.pine.dto.shorts.ShortsMainDto;
import com.site.pine.dto.shorts.ShortsResDto;
import com.site.pine.dto.shorts.ShortsUpdateReqDto;
import com.site.pine.dto.shorts.ShortsUploadReqDto;
import com.site.pine.dto.tag.TagResDto;
import com.site.pine.entity.File;
import com.site.pine.entity.Member;
import com.site.pine.entity.post.Post;
import com.site.pine.entity.shorts.ShortsPost;
import com.site.pine.entity.shorts.ShortsViewHistory;
import com.site.pine.event.ShortsMediaEvent;
import com.site.pine.repository.*;
import com.site.pine.repository.like.PostLikeRepository;
import com.site.pine.repository.like.ReplyLikeRepository;
import com.site.pine.repository.shorts.ShortsPostRepository;
import com.site.pine.repository.shorts.ShortsViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 🔧 수정: Spring Tx로 통일 권장
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortsService {

    private final ShortsPostRepository spr;
    private final PostRepository pr;
    private final MemberRepository mr;
    private final FileRepository fr;
    private final S3UploadService sus;

    @Value("${ffmpeg.path}")
    private String ffmpegPath;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final ShortsAsyncService sas;
    private final TagService ts;
    private final ShortsViewRepository svr;

    private final ReplyRepository rr;
    private final ReplyLikeRepository rlr;
    private final PostLikeRepository plr;
    private final TagMappingRepository tmr;

    @Transactional(readOnly = true)
    public HashMap<String, Object> getAllShorts(MemberDto memberdto, int page) {
        HashMap<String, Object> result = new HashMap<>();

        Pageable pageable = PageRequest.of(page, 2);

        // 로그인 여부 체크하여 ID 또는 null 전달
        Long loginId = (memberdto != null) ? memberdto.getId() : null;

        // 1️ 쇼츠 메인 DTO 조회 (엔티티 x)
        Page<ShortsMainDto> postPages = pr.getAllShortsPostList(loginId, pageable);

        List<ShortsMainDto> posts = postPages.getContent();

        // 2️ 쇼츠 ID 수집
        List<Long> postIds = posts.stream()
                .map(ShortsMainDto::getPostId)
                .toList();

        if (!postIds.isEmpty()) {

            // 3️ 파일 DTO 일괄 조회
            List<PostMainFileDto> files =
                    fr.findFilesByPostIds(postIds);
            Map<Long, List<PostMainFileDto>> fileMap =files.stream().collect(Collectors.groupingBy(PostMainFileDto::getPostId));

            // IN 절로 모든 태그 한번에 가져오기 (TagMappingRepository 쿼리 실행)
            List<TagResDto> allTags = ts.getTagsByPostIds(postIds);

            // Map으로 그룹핑: { 게시글ID : [태그명1, 태그명2, ...] }
            Map<Long, List<String>> tagMap = allTags.stream()
                    .collect(Collectors.groupingBy(
                            TagResDto::getTargetId, // 게시글 ID로 그룹핑
                            Collectors.mapping(TagResDto::getName, Collectors.toList()) // 태그 이름만 리스트로 수집
                    ));

            // 4️ 파일 주입
            for (ShortsMainDto post : posts) {
                if (fileMap.containsKey(post.getPostId())) {
                    fileMap.get(post.getPostId()).forEach(post::addFile);
                }

                // 태그 주입 (Map에서 꺼내서 세팅)
                // getOrDefault를 써서 태그가 없으면 빈 리스트([])를 넣어준다 (Null 방지)
                post.setTags(tagMap.getOrDefault(post.getPostId(), new ArrayList<>()));
            }

        }

        result.put("shortsList", posts);
        result.put("totalPage", postPages.getTotalPages());
        return result;
    }

    @Transactional
    public void insertShorts(ShortsUploadReqDto dto, MemberDto memberdto) {

        MultipartFile video = dto.getVideoFile();
        MultipartFile thumbnail = dto.getThumbnailFile();

        Path tempVideo = null;      // catch에서 삭제하기 위해 밖으로 뺌
        Path tempManualThumb = null; // manual 썸네일일 경우 안전하게 파일로 만들어 넘김

        Member member = mr.findById(memberdto.getId()).orElseThrow(() -> new IllegalStateException("회원 정보가 없습니다."));

        try {
            // 1) Shorts 저장
            Post post = new Post();
            post.setContent(dto.getContent());
            post.setMember(member);
            pr.save(post);

            // 태그 저장 로직 추가
            if (dto.getTags() != null) {
                ts.updateTags(post.getId(), dto.getTags());
            }

            // 2) VIDEO File row 생성 (WAIT)
            File videoFile = new File();
            videoFile.setOriginalname(video.getOriginalFilename());
            videoFile.setContentType(video.getContentType());
            videoFile.setSize(0L);
            videoFile.setPath("/images/GwakCheol-i.png");
            videoFile.setStatus(0); // WAIT
            videoFile.setPost(post);
            fr.save(videoFile);

            // 3) THUMBNAIL File row 생성 (WAIT)
            File thumbFile = new File();
            thumbFile.setOriginalname("auto_thumbnail.jpg"); // 기본값
            thumbFile.setContentType("image/jpeg");
            thumbFile.setSize(0L);
            thumbFile.setPath("/images/GwakCheol-i.png");
            thumbFile.setStatus(0); // WAIT
            thumbFile.setPost(post);
            fr.save(thumbFile);

            // 4) 요청 스레드에서 MultipartFile -> "내가 만든" 임시 파일로 복사
            tempVideo = Files.createTempFile("upload-video-", ".mp4");
            video.transferTo(tempVideo.toFile());

            // manual일 때도 MultipartFile을 비동기로 넘기지 않기 위해
            // 임시 썸네일 파일을 만들어 경로만 넘길 수 있음
            String thumbType = dto.getThumbnailType();
            String tempManualThumbPath = null;
            if ("manual".equals(thumbType) && thumbnail != null && !thumbnail.isEmpty()) {
                tempManualThumb = Files.createTempFile("upload-thumb-", ".jpg");
                thumbnail.transferTo(tempManualThumb.toFile());
                tempManualThumbPath = tempManualThumb.toString();
                thumbFile.setOriginalname(thumbnail.getOriginalFilename()); // 원래 이름 반영
            }

            // 5) AFTER_COMMIT 이벤트 발행 (경로 문자열만 전달)
            applicationEventPublisher.publishEvent(
                    new ShortsMediaEvent(
                            post.getId(),
                            videoFile.getId(),
                            thumbFile.getId(),
                            tempVideo.toString(),
                            thumbType,
                            tempManualThumbPath // manual일 때만 값 존재, auto면 null
                    )
            );

            ShortsPost shortsPost = new ShortsPost();
            shortsPost.setPost(post);
            shortsPost.setTitle(dto.getTitle());
            spr.save(shortsPost);

        } catch (Exception e) {
            // insert 단계에서 실패하면 임시파일 정리
            safeDelete(tempVideo);
            safeDelete(tempManualThumb);

            log.error("쇼츠 업로드 실패", e);
            throw new IllegalStateException("쇼츠 업로드 중 오류가 발생했습니다.");
        }
    }

    private void safeDelete(Path p) {
        try {
            if (p != null) Files.deleteIfExists(p);
        } catch (Exception ignored) {}
    }

    @Transactional(readOnly = true)
    public ShortsResDto getShortsDetail(Long postId) {
        ShortsPost shortsPost = spr.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 쇼츠가 존재하지 않습니다. id=" + postId));
        // 태그 리스트 조회해서 DTO에 넣기
        List<String> tags = ts.getTags(postId);

        return ShortsResDto.from(shortsPost, tags);
    }

    // 쇼츠 재생수 증가(비동기 처리)
    @Async("taskExecutor") // AsyncConfig에 등록된 Bean 이름 (보통 taskExecutor)
    @Transactional
    public void countView(Long shortsId, Long memberId, String cookie) {

        // 쿨타임 기준 시간 설정 (현재 시간 - 10분)
        LocalDateTime timeLimit = LocalDateTime.now().minusMinutes(10);

        // 중복 조회 체크
        boolean isDuplicate;
        if (memberId != null) {
            // 회원: (쇼츠ID + 회원ID)로 체크
            isDuplicate = svr.existsByMemberRecent(shortsId, memberId, timeLimit);
        } else {
            // 비회원: (쇼츠ID + 쿠키)로 체크
            isDuplicate = svr.existsByCookieRecent(shortsId, cookie, timeLimit);
        }

        // 중복이면 로직 종료 (DB 쓰기 방지)
        if (isDuplicate) return;

        // 기록 저장
        // getReferenceById: 실제 조회 쿼리 없이 Proxy 객체만 가져옴 (성능 최적화)
        ShortsPost shortsPost = spr.getReferenceById(shortsId);
        Member member = (memberId != null) ? mr.getReferenceById(memberId) : null;

        ShortsViewHistory history = new ShortsViewHistory(shortsPost, member, cookie);
        svr.save(history);

        // 조회수 증가 (Atomic Update)
        // DB 쿼리로 직접 +1 실행 (동시성 해결)
        spr.increaseViewCount(shortsId);
    }

    @Transactional
    public void updateShorts(ShortsUpdateReqDto dto, Long loginMemberId) {
        // ShortsPost 조회 (조인된 Post 정보도 필요함)
        ShortsPost shortsPost = spr.findById(dto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 작성자 권한 체크
        if (!shortsPost.getPost().getMember().getId().equals(loginMemberId)) {
            throw new IllegalArgumentException("본인의 게시글만 수정할 수 있습니다.");
        }

        // 데이터 업데이트
        shortsPost.setTitle(dto.getTitle());
        shortsPost.getPost().setContent(dto.getContent());

        // 태그 업데이트
        if (dto.getTags() != null) {
            ts.updateTags(dto.getPostId(), dto.getTags());
        }

        // 썸네일 업데이트 (Optional)
        MultipartFile newThumb = dto.getThumbnailFile();
        if (newThumb != null && !newThumb.isEmpty()) {

            // Post ID를 기준으로 기존 이미지 파일 조회 (PostMainFileDto 로직 참고)
            Long targetPostId = shortsPost.getPost().getId();

            File oldThumbFile = fr.findByPostIdAndContentTypeStartingWith(targetPostId, "image/")
                    .stream().findFirst().orElse(null);

            if (oldThumbFile != null) {
                try {
                    // 기존 파일 S3 삭제 직접 지우지 않고 이벤트를 통해 트랜잭션 성공 후 삭제되도록 위임
                    S3DeleteEventDto deleteEvent = new S3DeleteEventDto(
                            oldThumbFile.getOriginalname(),
                            oldThumbFile.getSize(),
                            oldThumbFile.getPath()
                    );
                    applicationEventPublisher.publishEvent(deleteEvent);

                    // 새 파일 업로드
                    String newPath = sus.saveFile(newThumb);

                    // DB 정보 갱신 (Dirty Checking)
                    oldThumbFile.setOriginalname(newThumb.getOriginalFilename());
                    oldThumbFile.setPath(newPath);
                    oldThumbFile.setSize(newThumb.getSize());
                    oldThumbFile.setContentType(newThumb.getContentType());

                } catch (Exception e) {
                    log.error("썸네일 변경 실패", e);
                }
            }
        }
    }

    @Transactional
    public void deleteShorts(Long postId, Long memberId) {

        ShortsPost shortsPost = spr.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        if (!shortsPost.getPost().getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        Post post = shortsPost.getPost();

        rr.unlinkRepliesByPost(post); // 대댓글 관계 끊기
        rlr.deleteAllByPost(post); // 댓글 좋아요 삭제
        rr.deleteAllByPost(post); // 댓글 삭제
        plr.deleteAllByPost(post); // 게시글 좋아요 삭제

        // S3 파일 삭제 (DB 삭제 전에 물리 파일부터 지움)
        List<File> files = fr.findAllByPost(post);
        if (!files.isEmpty()) {
            for (File file : files) {
                S3DeleteEventDto deleteEvent = new S3DeleteEventDto(
                        file.getOriginalname(),
                        file.getSize(),
                        file.getPath()
                );
                applicationEventPublisher.publishEvent(deleteEvent);
            }
            // 파일 DB 삭제
            fr.deleteAllByPost(post);
        }

        tmr.deleteByTargetId(post.getId()); // 해쉬태그 삭제
        svr.deleteAllByShortsPost(shortsPost); // 재생수 삭제

        spr.delete(shortsPost); // 게시물 삭제
        pr.delete(post); // 게시물 공통엔티티 삭제
    }



}
