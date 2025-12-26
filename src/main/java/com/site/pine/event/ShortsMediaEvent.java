package com.site.pine.event;

import org.springframework.web.multipart.MultipartFile;

/**
 * Shorts 업로드 후
 * 영상 압축 + 썸네일 생성을 요청하는 이벤트
 */
public record ShortsMediaEvent(
        Long shortsId,
        Long videoFileId,
        Long thumbFileId,
        String tempVideoPath,
        String thumbnailType
) {}

