package com.site.pine.service;

import com.site.pine.dto.post.PostMainFileDto;
import com.site.pine.dto.member.MemberDto;
import com.site.pine.dto.shorts.ShortsMainDto;
import com.site.pine.dto.shorts.ShortsUploadReqDto;
import com.site.pine.entity.File;
import com.site.pine.entity.Member;
import com.site.pine.entity.post.Post;
import com.site.pine.entity.shorts.ShortsPost;
import com.site.pine.event.ShortsMediaEvent;
import com.site.pine.repository.FileRepository;
import com.site.pine.repository.MemberRepository;
import com.site.pine.repository.PostRepository;
import com.site.pine.repository.shorts.ShortsPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 🔧 수정: Spring Tx로 통일 권장
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
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

    @Transactional(readOnly = true)
    public HashMap<String, Object> getAllShorts(MemberDto memberdto, int page) {
        HashMap<String, Object> result = new HashMap<>();

        Pageable pageable = PageRequest.of(page, 2);

        // 1️ 쇼츠 메인 DTO 조회 (엔티티 x)
        Page<ShortsMainDto> postPages =
                pr.getAllShortsPostList(pageable);

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

            // 4️ 파일 주입
            for (ShortsMainDto post : posts) {
                if (fileMap.containsKey(post.getPostId())) {
                    fileMap.get(post.getPostId()).forEach(post::addFile);
                }
            }

            // 5️ 좋아요 여부 (로그인 유저만)
//            if (memberdto != null) {
//                for (ShortsMainDto dto : shortsList) {
//                    boolean liked = likeRepository.existsByMember_IdAndTargetTypeAndTargetId(
//                                    memberDto.getId(),
//                                    dto.getShortsId()
//                            );
//                    dto.setLiked(liked);
//                }
//            }
        }

        result.put("shortsList", posts);
        result.put("totalPage", postPages.getTotalPages());
        return result;
    }

    @Transactional
    public void insertShorts(ShortsUploadReqDto dto, MemberDto memberdto) {

        MultipartFile video = dto.getVideoFile();
        MultipartFile thumbnail = dto.getThumbnailFile();

        Path tempVideo = null;      // 🔧 수정: catch에서 삭제하기 위해 밖으로 뺌
        Path tempManualThumb = null; // 🔧 수정: manual 썸네일일 경우 안전하게 파일로 만들어 넘김(선택)

        Member member = mr.findById(memberdto.getId()).orElseThrow(() -> new IllegalStateException("회원 정보가 없습니다."));

        try {
            // 1) Shorts 저장
            Post post = new Post();
            post.setContent(dto.getContent());
            post.setMember(member);
            pr.save(post);

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

            // 4) 요청 스레드에서 MultipartFile -> "내가 만든" 임시 파일로 복사 (핵심)
            tempVideo = Files.createTempFile("upload-video-", ".mp4");
            video.transferTo(tempVideo.toFile());

            // 🔧 수정(선택): manual일 때도 MultipartFile을 비동기로 넘기지 않기 위해
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
                            tempManualThumbPath // 🔧 수정: manual일 때만 값 존재, auto면 null
                    )
            );

            ShortsPost shortsPost = new ShortsPost();
            shortsPost.setPost(post);
            shortsPost.setTitle(dto.getTitle());
            spr.save(shortsPost);

        } catch (Exception e) {
            // 🔧 수정: insert 단계에서 실패하면 임시파일 정리
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
}
