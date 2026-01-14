package com.site.pine.dto.shorts;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
public class ShortsBestDto {
    private Long id;
    private String title;
    private String nickname;
    private String thumbnailPath;
    private Long viewCount;
    private Timestamp writeDate;
    private String profileImg;


    public ShortsBestDto(Long id, String title, String nickname, String thumbnailPath, Long viewCount, Timestamp writeDate, String profileImg) {
        this.id = id;
        this.title = title;
        this.nickname = nickname;
        this.thumbnailPath = thumbnailPath;
        this.viewCount = viewCount;
        this.writeDate = writeDate;
        this.profileImg = profileImg;
    }
}

