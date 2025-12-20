package com.site.pine.service;

import com.site.pine.dto.FileDto;
import com.site.pine.dto.S3DeleteEventDto;
import com.site.pine.dto.shorts.ShortsResDto;
import com.site.pine.dto.shorts.ShortsUploadReqDto;
import com.site.pine.entity.File;
import com.site.pine.entity.shorts.Shorts;
import com.site.pine.repository.FileRepository;
import com.site.pine.repository.ShortsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class shortsService {

    private final ShortsRepository sr;
    private final FileRepository fr;
    private final S3UploadService sus;
    private long lastThumbnailSize; // 자동 썸네일 size 임시 보관용
    @Value("${ffmpeg.path}")
    private String ffmpegPath;
    private final ApplicationEventPublisher applicationEventPublisher;


    @Transactional
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


    @Transactional
    public void insertShorts(ShortsUploadReqDto dto) {

        // 업로드 파일 꺼내기
        MultipartFile video = dto.getVideoFile();
        MultipartFile thumbnail = dto.getThumbnailFile();

        // s3저장 경로 변수
        String videoPath = null;
        String thumbnailPath = null;

        try{
            // 1️ Shorts 저장
            Shorts shorts = new Shorts();
            shorts.setTitle(dto.getTitle());
            shorts.setContent(dto.getContent());
            sr.save(shorts);

            // 2️ 영상 S3 업로드
            videoPath = sus.saveFile(dto.getVideoFile());
            applicationEventPublisher.publishEvent(new S3DeleteEventDto(videoPath));
            saveFile(shorts, dto.getVideoFile(), videoPath, "shorts");

            // 3️ 썸네일 분기
            if ("manual".equals(dto.getThumbnailType())) {
                // s3업로드
                String thumbUrl = sus.saveFile(thumbnail);
                applicationEventPublisher.publishEvent(new S3DeleteEventDto(thumbUrl));
                saveFile(shorts, thumbnail, thumbUrl, "shortsThumbnail");

            } else if ("auto".equals(dto.getThumbnailType())) {
                // ★ 자동 썸네일
                String thumbUrl = createThumbnailFromVideo(video);
                applicationEventPublisher.publishEvent(new S3DeleteEventDto(thumbUrl));
                saveAutoThumbnail(shorts, thumbUrl, lastThumbnailSize);
            } else {
                throw new IllegalStateException("썸네일 타입이 올바르지 않습니다.");
            }

        } catch (IOException e){
            throw new IllegalStateException("파일 업로드 중 오류가 발생했습니다.");
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

    private String createThumbnailFromVideo(MultipartFile video) {

        Path tempVideoPath = null;
        Path tempThumbnailPath = null;

        try {
            String uuid = UUID.randomUUID().toString();

            tempVideoPath = Paths.get(System.getProperty("java.io.tmpdir"), uuid + ".mp4");
            tempThumbnailPath = Paths.get(System.getProperty("java.io.tmpdir"), uuid + ".jpg");

            //  MultipartFile → 로컬 임시 영상
            video.transferTo(tempVideoPath.toFile());

            // ffmpeg 실행
            ProcessBuilder pb = new ProcessBuilder(
                    ffmpegPath,
                    "-ss", "00:00:00.1",
                    "-i", tempVideoPath.toString(),
                    "-vf", "scale=720:1280",
                    "-vframes", "1",
                    tempThumbnailPath.toString()
            );

            pb.redirectErrorStream(true);
            Process process = pb.start();

            //  ffmpeg 로그 출력 (디버깅용)
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println("[ffmpeg] " + line);
                }
            }

            //  ffmpeg 정상 종료 체크
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IllegalStateException("ffmpeg 실행 실패 (exitCode=" + exitCode + ")");
            }

            // 썸네일 파일 생성 여부 + 0 byte 방어
            if (!Files.exists(tempThumbnailPath) || Files.size(tempThumbnailPath) == 0) {
                throw new IllegalStateException("썸네일 생성 실패 (0 byte)");
            }

            // 썸네일 사이즈 기록 (DB 저장용)
            lastThumbnailSize = Files.size(tempThumbnailPath);

            // 로컬 썸네일 → S3 업로드
            return sus.saveLocalFile(tempThumbnailPath);

        } catch (Exception e) {
            throw new IllegalStateException("썸네일 생성 실패", e);

        } finally {
            // 임시 파일 정리!
            try {
                if (tempVideoPath != null) {
                    Files.deleteIfExists(tempVideoPath);
                    System.out.println("임시 영상 삭제 완료: " + tempVideoPath);
                }
                if (tempThumbnailPath != null) {
                    Files.deleteIfExists(tempThumbnailPath);
                    System.out.println("임시 썸네일 삭제 완료: " + tempThumbnailPath);
                }
            } catch (IOException e) {
                System.out.println("임시 파일 삭제 실패: " + e.getMessage());
            }
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


}
