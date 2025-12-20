package com.site.pine.event;

/**
 * Shorts 업로드 트랜잭션이 끝난 뒤
 * 자동 썸네일 생성을 요청하기 위한 이벤트
 */
public record ShortsThumbnailEvent(
        Long fileId,     // 썸네일 File PK
        String videoPath // S3에 올라간 영상 경로
) {}
