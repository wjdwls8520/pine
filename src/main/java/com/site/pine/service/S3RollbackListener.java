package com.site.pine.service;

import com.site.pine.dto.S3DeleteEventDto;
import com.site.pine.entity.S3FileDeleteFailList;
import com.site.pine.mapper.S3FileDeleteFailMapper;
import com.site.pine.repository.group.S3FileDeleteFailListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3RollbackListener {
    private final S3UploadService sus;
    private final S3FileDeleteFailListRepository sfdfr;
    private final S3FileDeleteFailMapper sfdfm;

    /**
     * 트랜잭션이 롤백되었을 때(업로드 실패 등) S3에 올라간 파일을 삭제함.
     * 중요: 이미 상위 트랜잭션은 롤백되어 죽었으므로, 실패 로그를 DB에 저장하려면
     * 반드시 '새로운 트랜잭션(REQUIRES_NEW)'을 열어야 함.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    @Transactional(propagation = Propagation.REQUIRES_NEW) // 🔥 필수: 새 트랜잭션 시작
    public void rollback(S3DeleteEventDto event) {
        log.info("Transaction Rollback detected. Deleting S3 file: {}", event.getPath());

        try {
            // 중복 호출 제거하고 try 안으로 넣음
            sus.deleteFile(event.getPath());
            log.info("S3 File deleted successfully.");

        } catch (Exception e) {
            log.error("Failed to delete S3 file during rollback. Saving to FailList.", e);

            // S3 삭제 실패 시, 별도 테이블에 기록 (추후 배치로 삭제)
            // REQUIRES_NEW 덕분에 롤백된 트랜잭션과 무관하게 저장이 성공함.
            S3FileDeleteFailList s3FileDeleteFailList = sfdfm.toS3FileDeleteFailMapper(event, e);
            sfdfr.save(s3FileDeleteFailList);
        }
    }
}