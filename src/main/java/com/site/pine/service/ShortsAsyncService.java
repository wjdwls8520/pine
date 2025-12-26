package com.site.pine.service;

import com.site.pine.event.ShortsMediaEvent;
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

    private final ShortsMediaTxService txService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMediaEvent(ShortsMediaEvent event) {

        log.info("[EVENT RECEIVED] shortsId={}, thread={}",
                event.shortsId(),
                Thread.currentThread().getName());

        // 핵심: 트랜잭션 서비스로 위임
        txService.processMediaTx(event);
    }
}
