package com.site.pine.dto.shorts;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ShortsMainDto {

    private Long shortsId;
    private String title;
    private String content;
    private Timestamp indate;
    private Timestamp updateDate;

    private Long memberId;
    private String nickname;
    private String profileImg;

    private boolean liked;

    private List<ShortsFileDto> files = new ArrayList<>();

    public ShortsMainDto(
            Long shortsId,
            String title,
            String content,
            Timestamp indate,
            Timestamp updateDate,
            Long memberId,
            String nickname,
            String profileImg
    ) {
        this.shortsId = shortsId;
        this.title = title;
        this.content = content;
        this.indate = indate;
        this.updateDate = updateDate;
        this.memberId = memberId;
        this.nickname = nickname;
        this.profileImg = profileImg;
    }

    public void addFile(ShortsFileDto file) {
        this.files.add(file);
    }
}

