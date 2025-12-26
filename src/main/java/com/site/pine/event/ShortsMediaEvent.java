package com.site.pine.event;

/**
 * Shorts 업로드 후
 * 영상 압축 + 썸네일 생성을 요청하는 이벤트
 *
 * - MultipartFile X
 * - "임시 파일 경로 문자열"만 전달
 */
public record ShortsMediaEvent(
        Long shortsId,
        Long videoFileId,
        Long thumbFileId,
        String tempVideoPath,
        String thumbnailType,
        String tempManualThumbPath // 🔧 수정: manual일 때만 존재, auto면 null
) {}
