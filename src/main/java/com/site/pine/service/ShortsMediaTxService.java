package com.site.pine.service;

import com.site.pine.entity.File;
import com.site.pine.event.ShortsMediaEvent;
import com.site.pine.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShortsMediaTxService {

    private final FileRepository fr;
    private final S3UploadService sus;

    @Value("${ffmpeg.path}")
    private String ffmpegPath;

    /**
     * 🔥 핵심 트랜잭션 로직
     * - 로컬 임시 영상(Path)을 기준으로 처리
     * - MultipartFile ❌ 사용하지 않음
     */
    @Transactional
    public void processMediaTx(ShortsMediaEvent event) {

        // ✅ DB 기준은 File ID (shortsId ❌)
        File videoFile = fr.findById(event.videoFileId())
                .orElseThrow(() -> new IllegalStateException("video file 없음"));

        File thumbFile = fr.findById(event.thumbFileId())
                .orElseThrow(() -> new IllegalStateException("thumbnail file 없음"));

        Path tempVideo = null;
        Path tempCompressed = null;
        Path tempThumb = null;

        try {
            // ===============================
            // 1️⃣ 상태: 처리중
            // ===============================
            videoFile.setStatus(1); // PROCESSING
            thumbFile.setStatus(1);

            // ===============================
            // 2️⃣ 이미 만들어둔 로컬 임시 영상 사용
            // 🔧 수정 포인트
            // ===============================
            tempVideo = Path.of(event.tempVideoPath());

            if (!Files.exists(tempVideo)) {
                throw new IllegalStateException("임시 영상 파일이 존재하지 않음");
            }

            // ===============================
            // 3️⃣ ffmpeg 영상 압축
            // ===============================
            tempCompressed = Files.createTempFile("compressed-", ".mp4");
            runFfmpegCompress(tempVideo, tempCompressed);

            // ===============================
            // 4️⃣ ffmpeg 썸네일 생성
            // ===============================
            tempThumb = Files.createTempFile("thumb-", ".jpg");
            runFfmpegThumbnail(tempCompressed, tempThumb);

            // ===============================
            // 5️⃣ S3 업로드 (결과물만)
            // ===============================
            String videoPath = sus.saveLocalFile(tempCompressed);
            String thumbPath = sus.saveLocalFile(tempThumb);

            // ===============================
            // 6️⃣ DB 반영
            // ===============================
            videoFile.setPath(videoPath);
            videoFile.setSize(Files.size(tempCompressed));
            videoFile.setStatus(2); // DONE

            thumbFile.setPath(thumbPath);
            thumbFile.setSize(Files.size(tempThumb));
            thumbFile.setStatus(2); // DONE

            log.info("[MEDIA DONE] shortsId={}", event.shortsId());

        } catch (Exception e) {

            videoFile.setStatus(3); // FAIL
            thumbFile.setStatus(3);
            log.error("미디어 처리 실패 shortsId={}", event.shortsId(), e);
            throw new RuntimeException(e);

        } finally {
            // ===============================
            // 7️⃣ 임시 파일 정리
            // ===============================
            delete(tempVideo);
            delete(tempCompressed);
            delete(tempThumb);
        }
    }

    // ===============================
    // ffmpeg: 영상 압축
    // ===============================
    private void runFfmpegCompress(Path src, Path target) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
                ffmpegPath,
                "-i", src.toString(),
                "-vcodec", "libx264",
                "-preset", "veryfast",
                "-crf", "28",
                target.toString()
        );
        pb.start().waitFor();
    }

    // ===============================
    // ffmpeg: 썸네일 생성
    // ===============================
    private void runFfmpegThumbnail(Path src, Path target) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
                ffmpegPath,
                "-ss", "00:00:00.1",
                "-i", src.toString(),
                "-vframes", "1",
                target.toString()
        );
        pb.start().waitFor();
    }

    private void delete(Path p) {
        try {
            if (p != null && Files.exists(p)) {
                Files.deleteIfExists(p);
            }
        } catch (Exception ignored) {}
    }
}

