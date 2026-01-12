package com.site.pine.dto.shorts;

import com.site.pine.dto.FileDto;
import com.site.pine.entity.shorts.ShortsPost;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ShortsResDto {
    private Long postId;
    private String title;
    private String content;
    private Timestamp writeDate;
    private Timestamp updateDate;
    private String nickname;
    private String profileImg;

    private int likeCount;
    private int replyCount;
    private Long viewCount;

    private List<String> tags;
    private List<FileDto> files;

    // 엔티티를 DTO로 변환하는 팩토리 메서드
    public static ShortsResDto from(ShortsPost entity, List<String> tags) {
        ShortsResDto dto = new ShortsResDto();

        // 1. ShortsPost 정보 매핑
        dto.setPostId(entity.getPost().getId());
        dto.setTitle(entity.getTitle());
        dto.setViewCount(entity.getViewCount());

        // 2. 부모 Post 정보 매핑
        dto.setContent(entity.getPost().getContent());
        dto.setWriteDate(entity.getPost().getWriteDate());
        dto.setLikeCount(entity.getPost().getLikeCount());
        dto.setReplyCount(entity.getPost().getReplyCount());

        // 3. 작성자(Member) 정보 매핑
        if (entity.getPost().getMember() != null) {
            dto.setNickname(entity.getPost().getMember().getNickname());
            dto.setProfileImg(entity.getPost().getMember().getProfile_img());
        }

        // 태그주입
        dto.setTags(tags);

        // 4. 파일 정보 매핑 (File -> FileDto)
        List<FileDto> fileDtos = new ArrayList<>();
        if (entity.getPost().getFiles() != null) {
            fileDtos = entity.getPost().getFiles().stream()
                    .map(file -> {
                        FileDto fd = new FileDto();
                        fd.setPath(file.getPath());
                        fd.setOriginalname(file.getOriginalname());
                        fd.setContentType(file.getContentType());
                        return fd;
                    })
                    .collect(Collectors.toList());
        }
        dto.setFiles(fileDtos);

        return dto;
    }
}
