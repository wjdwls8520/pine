package com.site.pine.service;

import com.site.pine.event.ShortsThumbnailEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


@Slf4j
@Service
@RequiredArgsConstructor
public class ShortsAsyncService {

    private final ShortsThumbnailTxService txService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleThumbnailEvent(ShortsThumbnailEvent event) {

        log.info("[EVENT RECEIVED] fileId={}, thread={}",
                event.fileId(),
                Thread.currentThread().getName());

        // 핵심: 다른 Bean 호출
        txService.createThumbnailTx(
                event.fileId(),
                event.videoPath()
        );
    }
}
