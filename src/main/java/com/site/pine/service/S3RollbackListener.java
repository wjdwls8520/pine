package com.site.pine.service;

import com.site.pine.dto.S3DeleteEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3RollbackListener {
    private final S3UploadService sus;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void rollback(S3DeleteEventDto event) {
        log.error("🔥 AFTER_ROLLBACK listener fired. path={}", event.getPath());
        sus.deleteFile(event.getPath());
    }
}
