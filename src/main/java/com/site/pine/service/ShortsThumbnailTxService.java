package com.site.pine.service;

import com.site.pine.entity.File;
import com.site.pine.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShortsThumbnailTxService {

    private final FileRepository fr;
    private final S3UploadService sus;

    @Value("${ffmpeg.path}")
    private String ffmpegPath;

    /**
     * ✅ 트랜잭션 전용 메서드
     * - File 상태 변경
     * - 썸네일 생성 결과 DB 반영
     */
    @Transactional
    public void createThumbnailTx(Long fileId, String videoPath) {

        log.info("[TX START] fileId={}", fileId);

        File file = fr.findById(fileId)
                .orElseThrow(() -> new IllegalStateException("파일 정보 없음"));

        Path tempVideoPath = null;

        try {
            // 1️⃣ 처리중
            file.setStatus(1);

            // 2️⃣ S3 영상 → 로컬 임시 파일
            tempVideoPath = downloadVideoFromS3(videoPath);

            // 3️⃣ ffmpeg → 썸네일 생성
            ThumbnailResult result = createThumbnailFromLocalVideo(tempVideoPath);

            // 4️⃣ DB 반영
            file.setPath(result.path());
            file.setSize(result.size());
            file.setStatus(2); // DONE

            log.info("[TX DONE] fileId={}", fileId);

        } catch (Exception e) {
            file.setStatus(3); // FAIL
            log.error("썸네일 생성 실패 fileId={}", fileId, e);
            throw new RuntimeException(e);
        } finally {
            // 5️⃣ 임시 영상 정리
            if (tempVideoPath != null) {
                try {
                    Files.deleteIfExists(tempVideoPath);
                } catch (Exception ignored) {}
            }
        }
    }

    /**
     * S3 URL → 로컬 임시 파일 다운로드
     */
    private Path downloadVideoFromS3(String videoPath) throws Exception {

        Path tempVideoPath = Paths.get(
                System.getProperty("java.io.tmpdir"),
                UUID.randomUUID() + ".mp4"
        );

        try (InputStream in = new URL(videoPath).openStream()) {
            Files.copy(in, tempVideoPath);
        }

        return tempVideoPath;
    }

    /**
     * 로컬 영상 → ffmpeg 썸네일 생성 → S3 업로드
     */
    private ThumbnailResult createThumbnailFromLocalVideo(Path videoPath) throws Exception {

        Path tempThumbnailPath = Paths.get(
                System.getProperty("java.io.tmpdir"),
                UUID.randomUUID() + ".jpg"
        );

        ProcessBuilder pb = new ProcessBuilder(
                ffmpegPath,
                "-ss", "00:00:00.1",
                "-i", videoPath.toString(),
                "-vf", "scale=720:1280",
                "-vframes", "1",
                tempThumbnailPath.toString()
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            while (br.readLine() != null) {}
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IllegalStateException("ffmpeg 실행 실패");
        }

        if (!Files.exists(tempThumbnailPath) || Files.size(tempThumbnailPath) == 0) {
            throw new IllegalStateException("썸네일 생성 실패");
        }

        String s3Path = sus.saveLocalFile(tempThumbnailPath);
        long size = Files.size(tempThumbnailPath);

        Files.deleteIfExists(tempThumbnailPath);

        return new ThumbnailResult(s3Path, size);
    }

    /**
     * 썸네일 생성 결과 DTO
     */
    private record ThumbnailResult(String path, long size) {}
}
