package com.site.pine.service;

import com.site.pine.entity.File;
import com.site.pine.event.ShortsMediaEvent;
import com.site.pine.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
     * - 성공하면 DB(path/size/status) 반영
     * - 실패하면 status=3 + RuntimeException으로 롤백(네 방식 유지)
     */
    @Transactional
    public void processMediaTx(ShortsMediaEvent event) {

        File videoFile = fr.findById(event.videoFileId())
                .orElseThrow(() -> new IllegalStateException("video file 없음"));

        File thumbFile = fr.findById(event.thumbFileId())
                .orElseThrow(() -> new IllegalStateException("thumbnail file 없음"));

        Path tempVideo = null;
        Path tempCompressed = null;
        Path tempThumb = null;
        Path manualThumb = null;

        try {
            videoFile.setStatus(1); // PROCESSING
            thumbFile.setStatus(1);

            // 1) ShortsService에서 만들어둔 임시 영상
            tempVideo = Path.of(event.tempVideoPath());

            log.info("[MEDIA] tempVideo exists? {} / path={}",
                    Files.exists(tempVideo),
                    tempVideo
            );

            if (!Files.exists(tempVideo) || Files.size(tempVideo) == 0) {
                throw new IllegalStateException("임시 영상 파일이 존재하지 않거나 0 byte 입니다.");
            }

            // 2) ffmpeg 영상 압축
            tempCompressed = Files.createTempFile("compressed-", ".mp4");
            runFfmpegCompress(tempVideo, tempCompressed);

            if (!Files.exists(tempCompressed) || Files.size(tempCompressed) == 0) {
                throw new IllegalStateException("압축 결과 파일이 생성되지 않았거나 0 byte 입니다.");
            }

            // 3) 썸네일 처리 (manual / auto 분기)
            String thumbType = event.thumbnailType();

            if ("manual".equals(thumbType) && event.tempManualThumbPath() != null) {
                // 🔧 수정: manual이면 업로드된 썸네일 임시파일을 그대로 사용
                manualThumb = Path.of(event.tempManualThumbPath());
                if (!Files.exists(manualThumb) || Files.size(manualThumb) == 0) {
                    throw new IllegalStateException("manual 썸네일 임시 파일이 없거나 0 byte 입니다.");
                }
                tempThumb = manualThumb; // 업로드는 tempThumb로 통일
            } else {
                // auto: 영상에서 썸네일 생성
                tempThumb = Files.createTempFile("thumb-", ".jpg");
                runFfmpegThumbnail(tempCompressed, tempThumb);

                if (!Files.exists(tempThumb) || Files.size(tempThumb) == 0) {
                    throw new IllegalStateException("썸네일 생성 실패(0 byte)");
                }
            }

            // 4) S3 업로드 (결과물만)
            String videoPath = sus.saveLocalFile(tempCompressed);
            String thumbPath = sus.saveLocalFile(tempThumb);

            // 5) DB 반영
            videoFile.setPath(videoPath);
            videoFile.setSize(Files.size(tempCompressed));
            videoFile.setStatus(2); // DONE

            thumbFile.setPath(thumbPath);
            thumbFile.setSize(Files.size(tempThumb));
            thumbFile.setStatus(2); // DONE

            log.info("[MEDIA DONE] shortsId={}, videoFileId={}, thumbFileId={}",
                    event.shortsId(), event.videoFileId(), event.thumbFileId());

        } catch (Exception e) {
            videoFile.setStatus(3); // FAIL
            thumbFile.setStatus(3);

            log.error("미디어 처리 실패 shortsId={}, videoFileId={}, thumbFileId={}",
                    event.shortsId(), event.videoFileId(), event.thumbFileId(), e);

            // 네 예외처리 스타일 유지(실패 시 롤백)
            throw new RuntimeException(e);

        } finally {
            // 6) 임시 파일 정리
            // 🔧 수정: manualThumb는 ShortsService가 만든 임시파일이므로 여기서도 정리해줘도 OK
            safeDelete(tempVideo);
            safeDelete(tempCompressed);

            // manualThumb를 tempThumb로 공유했을 수 있으니 중복 삭제 방어
            if (manualThumb != null) {
                safeDelete(manualThumb);
            } else {
                safeDelete(tempThumb);
            }
        }
    }

    // ===============================
    // ffmpeg: 영상 압축
    // ===============================
    private void runFfmpegCompress(Path src, Path target) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
                ffmpegPath,
                "-y",                       // 🔧 수정: 덮어쓰기(없으면 실패하는 경우 있음)
                "-i", src.toString(),
                "-c:v", "libx264",
                "-preset", "veryfast",
                "-crf", "28",
                "-c:a", "aac",              // 🔧 수정: 오디오도 인코딩 지정(환경에 따라 필요)
                target.toString()
        );

        int exit = runAndLog(pb, "[ffmpeg-compress]");
        if (exit != 0) {
            throw new IllegalStateException("ffmpeg 압축 실패 (exitCode=" + exit + ")");
        }
    }

    // ===============================
    // ffmpeg: 썸네일 생성
    // ===============================
    private void runFfmpegThumbnail(Path src, Path target) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
                ffmpegPath,
                "-y",
                "-ss", "00:00:00.1",
                "-i", src.toString(),
                "-vframes", "1",
                target.toString()
        );

        int exit = runAndLog(pb, "[ffmpeg-thumb]");
        if (exit != 0) {
            throw new IllegalStateException("ffmpeg 썸네일 실패 (exitCode=" + exit + ")");
        }
    }

    /**
     * 🔧 수정: ffmpeg 로그를 INFO로도 남겨서
     * "왜 실패했는지"가 콘솔에 보이게 함
     */
    private int runAndLog(ProcessBuilder pb, String tag) throws Exception {
        pb.redirectErrorStream(true);
        Process p = pb.start();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                // 너무 많으면 debug로 내리고, 지금은 원인 찾는 중이라 info 추천
                log.info("{} {}", tag, line);
            }
        }

        return p.waitFor();
    }

    private void safeDelete(Path p) {
        try {
            if (p != null) Files.deleteIfExists(p);
        } catch (Exception ignored) {}
    }
}
