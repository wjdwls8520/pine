package com.site.pine.dto.search;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
public class SearchPostDto {
    private Long id;
    private String title;
    private String content;
    private String writerNickname;
    private String writerProfile;
    private Timestamp writeDate;
    private String thumbnail;
    private Integer likeCount;
    private Integer replyCount;

    private Long groupId;

    // JPQL 생성자
    // 순서: id, title, content, nick, profile, date, thumb, like, reply, groupId
    public SearchPostDto(Long id, String title, String content, String nickname, String profileImg, Timestamp writeDate, String thumbnail, Integer likeCount, Integer replyCount, Long groupId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.writerNickname = nickname;
        this.writerProfile = profileImg;
        this.writeDate = writeDate;
        this.thumbnail = thumbnail;
        this.likeCount = likeCount;
        this.replyCount = replyCount;
        this.groupId = groupId;
    }
}