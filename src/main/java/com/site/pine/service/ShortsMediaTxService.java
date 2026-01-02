package com.site.pine.service;

import com.site.pine.entity.File;
import com.site.pine.event.ShortsMediaEvent;
import com.site.pine.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager; // 추가
import org.springframework.transaction.support.TransactionTemplate; // 추가

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

    // 트랜잭션 매니저 주입 (수동 제어를 위해 필요)
    private final PlatformTransactionManager transactionManager;

    @Value("${ffmpeg.path}")
    private String ffmpegPath;

    /**
     *  핵심 트랜잭션 로직
     * - @Transactional 어노테이션 제거 (전체 메서드가 DB 연결을 물고 있지 않게 함)
     * - DB 업데이트가 필요한 순간에만 TransactionTemplate 사용
     */
    public void processMediaTx(ShortsMediaEvent event) {

        Path tempVideo = null;
        Path tempCompressed = null;
        Path tempThumb = null;
        Path manualThumb = null;

        // [STEP 1] 시작 상태 업데이트 (짧은 트랜잭션)
        // 로직 시작 전 '처리중'으로 변경하고 즉시 커밋 (유저에게 바로 보임)
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            File videoFile = fr.findById(event.videoFileId())
                    .orElseThrow(() -> new IllegalStateException("video file 없음"));
            File thumbFile = fr.findById(event.thumbFileId())
                    .orElseThrow(() -> new IllegalStateException("thumbnail file 없음"));

            videoFile.setStatus(1); // PROCESSING
            thumbFile.setStatus(1);
        });

        try {
            // [STEP 2] 무거운 작업 (트랜잭션 없이 실행 -> DB 커넥션 사용 X)

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
                // manual이면 업로드된 썸네일 임시파일을 그대로 사용
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

            long videoSize = Files.size(tempCompressed);
            long thumbSize = Files.size(tempThumb);

            // [STEP 3] 성공 상태 업데이트 (짧은 트랜잭션)
            // 업로드 완료 후 정보를 업데이트하고 즉시 커밋
            new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
                File videoFile = fr.findById(event.videoFileId())
                        .orElseThrow(() -> new IllegalStateException("video file 없음"));
                File thumbFile = fr.findById(event.thumbFileId())
                        .orElseThrow(() -> new IllegalStateException("thumbnail file 없음"));

                videoFile.setPath(videoPath);
                videoFile.setSize(videoSize);
                videoFile.setStatus(2); // DONE

                thumbFile.setPath(thumbPath);
                thumbFile.setSize(thumbSize);
                thumbFile.setStatus(2); // DONE
            });

            log.info("[MEDIA DONE] shortsId={}, videoFileId={}, thumbFileId={}",
                    event.postId(), event.videoFileId(), event.thumbFileId());

        } catch (Exception e) {
            // [STEP 4] 실패 상태 업데이트 (짧은 트랜잭션)
            new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
                try {
                    File videoFile = fr.findById(event.videoFileId())
                            .orElseThrow(() -> new IllegalStateException("video file 없음"));
                    File thumbFile = fr.findById(event.thumbFileId())
                            .orElseThrow(() -> new IllegalStateException("thumbnail file 없음"));

                    videoFile.setStatus(3); // FAIL
                    thumbFile.setStatus(3);
                } catch (Exception ex) {
                    log.error("실패 상태 업데이트 중 오류 발생", ex);
                }
            });

            log.error("미디어 처리 실패 shortsId={}, videoFileId={}, thumbFileId={}",
                    event.postId(), event.videoFileId(), event.thumbFileId(), e);

            throw new RuntimeException(e);

        } finally {
            // 6) 임시 파일 정리
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
                "-y",
                "-i", src.toString(),
                "-c:v", "libx264",
                "-preset", "veryfast",
                "-crf", "26",
                "-c:a", "aac",
                "-b:a", "128k",
                "-movflags", "+faststart",
                target.toString()
        );

        int exit = runAndLog(pb, "[ffmpeg-compress]");

        Thread.sleep(50); // Windows 안정화용

        long size = Files.exists(target) ? Files.size(target) : 0;

        if (exit != 0 || size < 1024) { // 1KB 미만은 실패로 간주
            throw new IllegalStateException(
                    "ffmpeg 압축 실패 or 결과 이상. size=" + size
            );
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

    private int runAndLog(ProcessBuilder pb, String tag) throws Exception {
        pb.redirectErrorStream(true);
        Process p = pb.start();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
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