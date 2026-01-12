package com.site.pine.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostAllDto {
    private Long id;

    private Long memberId;
    private String nickname;
    private String profile_img;

    private String content;
    private Integer replyCount;
    private Integer likeCount;

    private String fileSrc;

    public PostAllDto(Long id, Long memberId, String nickname, String profile_img, String content, Integer replyCount, Integer likeCount, String fileSrc) {
        this.id = id;
        this.memberId = memberId;
        this.nickname = nickname;
        this.profile_img = profile_img;
        this.content = content;
        this.replyCount = replyCount;
        this.likeCount = likeCount;
        this.fileSrc = fileSrc;
    }
}
