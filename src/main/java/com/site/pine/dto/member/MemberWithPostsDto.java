package com.site.pine.dto.member;

import com.site.pine.entity.Member;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

// MemberWithPostsDto.java (새로 생성)
@Getter
public class MemberWithPostsDto {
    private Long memberId;
    private String nickname;
    private String profileImg;
    private String profileMsg;

    // 핵심: 멤버 1명 안에 게시글 리스트가 들어감
    private List<MemberPostItemDto> posts = new ArrayList<>();

    // 생성자에서 멤버 정보 세팅
    public MemberWithPostsDto(Member member) {
        this.memberId = member.getId();
        this.nickname = member.getNickname();
        this.profileImg = member.getProfile_img(); // 엔티티 필드명 확인
        this.profileMsg = member.getProfile_msg();
    }

    // 게시글 추가 메서드
    public void addPost(MemberPostItemDto post) {
        this.posts.add(post);
    }
}
