package com.site.pine.dto.community;

import com.site.pine.dto.tag.TagResDto;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
public class PostMainListDto {

    // Post
    private Long postId;
    private String content;
    private Integer category;
    private Integer likeCount;
    private Integer replyCount;
    private Timestamp writeDate;

    // Member
    private Long memberId;
    private String nickname;
    private String profileImg;

    private boolean liked; // 로그인한 사용자가 좋아요 눌렀는지

    // ✅ Files (여러 개)
    private List<PostMainFileDto> files = new ArrayList<>();

    //태그
    private List<TagResDto> tags = new ArrayList<>();

    //  JPQL 생성자
    public PostMainListDto(
            Long postId,
            String content,
            Integer category,
            Integer likeCount,
            Integer replyCount,
            Timestamp writeDate,
            Long memberId,
            String nickname,
            String profileImg
    ) {
        this.postId = postId;
        this.content = content;
        this.category = category;
        this.likeCount = likeCount;
        this.replyCount = replyCount;
        this.writeDate = writeDate;
        this.memberId = memberId;
        this.nickname = nickname;
        this.profileImg = profileImg;
    }

    //  Service에서 파일 주입용
    public void addFile(PostMainFileDto file) {
        this.files.add(file);
    }

    //  태그 주입용
    public void addTag(TagResDto tag) {
        this.tags.add(tag);
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }
}
