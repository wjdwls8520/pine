package com.site.pine.dto.shorts;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ShortsBestDto {
    private Long id;
    private String title;
    private String nickname;
    private String thumbnailPath;

    public ShortsBestDto(Long id, String title, String nickname, String thumbnailPath) {
        this.id = id;
        this.title = title;
        this.nickname = nickname;
        this.thumbnailPath = thumbnailPath;
    }
}

