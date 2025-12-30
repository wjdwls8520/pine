package com.site.pine.dto.shorts;

import com.site.pine.dto.post.PostMainFileDto;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ShortsMainDto {

    private Long postId;
    private String title;
    private String content;
    private Timestamp writeDate;
    private Timestamp updateDate;

    private Long memberId;
    private String nickname;
    private String profileImg;

    private boolean liked;

    private List<PostMainFileDto> files = new ArrayList<PostMainFileDto>();

    public ShortsMainDto(
            Long postId,
            String title,
            String content,
            Timestamp writeDate,
            Timestamp updateDate,
            Long memberId,
            String nickname,
            String profileImg
    ) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.writeDate = writeDate;
        this.updateDate = updateDate;
        this.memberId = memberId;
        this.nickname = nickname;
        this.profileImg = profileImg;
    }

    public void addFile(PostMainFileDto file) {
        this.files.add(file);
    }
}

