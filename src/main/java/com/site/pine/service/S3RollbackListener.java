package com.site.pine.service;

import com.site.pine.dto.S3DeleteEventDto;
import com.site.pine.entity.S3FileDeleteFailList;
import com.site.pine.mapper.S3FileDeleteFailMapper;
import com.site.pine.repository.group.S3FileDeleteFailListRepository;
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
    private final S3FileDeleteFailListRepository sfdfr;
    private final S3FileDeleteFailMapper sfdfm;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void rollback(S3DeleteEventDto event) {
        sus.deleteFile(event.getPath());

        try {
            sus.deleteFile(event.getPath());
        } catch (Exception e) {
            // 알수없는 이유로 s3 이미지 삭제가 실패 했을때 고아파일의 정보를 추적 / db에 저장 추후 관리자 페이지에서 고아파일들을 삭제할 수 있게 적용.
            S3FileDeleteFailList s3FileDeleteFailList = sfdfm.toS3FileDeleteFailMapper(event, e);
            sfdfr.save(s3FileDeleteFailList);
        }
    }
}
