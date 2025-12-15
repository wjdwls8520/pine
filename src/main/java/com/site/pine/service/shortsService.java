package com.site.pine.service;

import com.site.pine.dto.FileDto;
import com.site.pine.dto.community.PostResDto;
import com.site.pine.dto.shorts.ShortsResDto;
import com.site.pine.dto.shorts.ShortsUploadReqDto;
import com.site.pine.entity.File;
import com.site.pine.entity.post.Post;
import com.site.pine.entity.shorts.Shorts;
import com.site.pine.repository.FileRepository;
import com.site.pine.repository.ShortsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class shortsService {

    private final ShortsRepository sr;
    private final FileRepository fr;
    private final S3UploadService sus;
    // 자동 썸네일 size 임시 보관용
    private long lastThumbnailSize;

    public void insertShorts(ShortsUploadReqDto dto) throws IOException {

        // ★ 변경: List<MultipartFile> → 개별 필드
        MultipartFile video = dto.getVideoFile();
        MultipartFile thumbnail = dto.getThumbnailFile();

        // ★ 영상 필수 체크 (이제 훨씬 명확)
        if (video == null || video.isEmpty()) {
            throw new IllegalArgumentException("영상 파일은 필수입니다.");
        }

        // 1️ Shorts 저장
        Shorts shorts = new Shorts();
        shorts.setTitle(dto.getTitle());
        shorts.setContent(dto.getContent());
        sr.save(shorts);

        // 2️ 영상 S3 업로드
        String videoUrl = sus.saveFile(video);
        saveFile(shorts, video, videoUrl, "shorts");

        // 3️ 썸네일 분기
        if ("manual".equals(dto.getThumbnailType())) {

            // ★ 수동 썸네일은 반드시 있어야 함
            if (thumbnail == null || thumbnail.isEmpty()) {
                throw new IllegalArgumentException("썸네일 파일이 없습니다.");
            }

            String thumbUrl = sus.saveFile(thumbnail);
            saveFile(shorts, thumbnail, thumbUrl, "shortsThumbnail");

        } else {
            // ★ 자동 썸네일
            String thumbUrl = createThumbnailFromVideo(videoUrl);
            saveAutoThumbnail(shorts, thumbUrl, lastThumbnailSize);
        }
    }


    private void saveFile(
            Shorts shorts,
            MultipartFile file,
            String path,
            String pageType
    ) {
        File f = new File();
        f.setPageType(pageType);
        f.setOriginalname(file.getOriginalFilename());
        f.setSize(file.getSize());
        f.setPath(path);
        f.setContentType(file.getContentType());
        f.setShorts(shorts);

        fr.save(f);
    }


    private void saveAutoThumbnail(Shorts shorts, String path, long size) {
        File f = new File();
        f.setPageType("shortsThumbnail");
        f.setOriginalname("auto_thumbnail.jpg");
        f.setSize(size);
        f.setPath(path);
        f.setContentType("image/jpeg");
        f.setShorts(shorts);

        fr.save(f);
    }

    private String createThumbnailFromVideo(String videoUrl) {

        Path tempVideoPath = null;
        Path tempThumbnailPath = null;

        try {
            String uuid = UUID.randomUUID().toString();

            tempVideoPath = Paths.get(
                    System.getProperty("java.io.tmpdir"),
                    uuid + ".mp4"
            );

            tempThumbnailPath = Paths.get(
                    System.getProperty("java.io.tmpdir"),
                    uuid + ".jpg"
            );

            // S3 → 로컬
            sus.downloadFile(videoUrl, tempVideoPath);

            // ffmpeg 실행
            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg",
                    "-ss", "00:00:00.1",
                    "-i", tempVideoPath.toString(),
                    "-vframes", "1",
                    tempThumbnailPath.toString()
            );

            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println("[ffmpeg] " + line);
                }
            }

            //  ffmpeg 성공 여부 체크
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("ffmpeg 실행 실패");
            }

            //  0 byte 방어
            if (!Files.exists(tempThumbnailPath) || Files.size(tempThumbnailPath) == 0) {
                throw new RuntimeException("썸네일 생성 실패 (0 byte)");
            }

            lastThumbnailSize = Files.size(tempThumbnailPath);

            // 썸네일 → S3
            String thumbnailUrl = sus.saveLocalFile(tempThumbnailPath);

            return thumbnailUrl;

        } catch (Exception e) {
            throw new RuntimeException("썸네일 생성 실패", e);
        } finally {
            // 임시 파일 정리
            try {
                if (tempVideoPath != null) Files.deleteIfExists(tempVideoPath);
                if (tempThumbnailPath != null) Files.deleteIfExists(tempThumbnailPath);
            } catch (IOException ignored) {}
        }
    }



//    public void insertShorts(ShortsUploadReqDto shortsuploadreqdto) throws IOException {
//        Shorts shortsEntity = new Shorts();
//        shortsEntity.setTitle(shortsuploadreqdto.getTitle());
//        shortsEntity.setContent(shortsuploadreqdto.getContent());
//        sr.save(shortsEntity);
//
//        for (MultipartFile file : shortsuploadreqdto.getFiles()) {
//
//            String fileUrl = sus.saveFile(file); // S3 업로드
//
//            File fileEntity = new File();
//            fileEntity.setPageType("shorts");
//            fileEntity.setOriginalname(file.getOriginalFilename());
//            fileEntity.setSize(file.getSize());
//            fileEntity.setPath(fileUrl);
//            fileEntity.setContentType(file.getContentType());
//            fileEntity.setShorts(shortsEntity);
//
//            fr.save(fileEntity);
//        }
//
//    }

    public HashMap<String, Object> getAllShorts(int page) {
        //1. 빈 해시맵 만들기
        HashMap<String, Object> result = new HashMap<>();
        // 2. 빈 리스트 만들기
        List<ShortsResDto> list = new ArrayList<>();

        // 3. 페이지 정의 = page번째 페이지에서 6 개씩 가져와라 라는 정보 담음
        Pageable pageable = PageRequest.of(page, 2);
        // 4. 페이지객체에 쇼츠엔티티 넣기 (페이지네이션 된 데이터만 가져옴)
        // Page<Shorts> 에는 페이지정보가 포함되어있음.
//        Page<Shorts> shortsPages = sr.findAllByOrderByIndateDesc(pageable);
        Page<Shorts> shortsPages = sr.findAllByOrderByIndateDescIdDesc(pageable);
        for(Shorts shortsEntity : shortsPages) {
            ShortsResDto resDto = new ShortsResDto();
            resDto.setId(shortsEntity.getId());
            resDto.setTitle(shortsEntity.getTitle());
            resDto.setContent(shortsEntity.getContent());
            resDto.setIndate(shortsEntity.getIndate());
            resDto.setUpdateDate(shortsEntity.getUpdateDate());

            List<FileDto> fileDtoList = new ArrayList<>();
            for( File file : shortsEntity.getFiles()) {
                FileDto fileDto = new FileDto();
                fileDto.setId(file.getId());
                fileDto.setPageType(file.getPageType());
                fileDto.setOriginalname(file.getOriginalname());
                fileDto.setPath(file.getPath());
                fileDto.setContentType(file.getContentType());
                fileDto.setSize(file.getSize());
                fileDtoList.add(fileDto);
            }

            resDto.setFiles(fileDtoList);
            list.add(resDto);
        }
        result.put("shortsList", list);
        result.put("totalPage", shortsPages.getTotalPages());

        return result;
    }

}
