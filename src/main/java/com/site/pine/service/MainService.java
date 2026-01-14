package com.site.pine.service;

import com.site.pine.dto.group.GroupContentsJpqlResDto;
import com.site.pine.dto.member.MemberAndCPostResDto;
import com.site.pine.dto.member.MemberPostItemDto;
import com.site.pine.dto.member.MemberWithPostsDto;
import com.site.pine.dto.post.PostAllDto;
import com.site.pine.dto.shorts.ShortsBestDto;
import com.site.pine.entity.Member;
import com.site.pine.repository.MemberRepository;
import com.site.pine.repository.PostRepository;
import com.site.pine.repository.community.CommunityPostRepository;
import com.site.pine.repository.group.GroupContentsRepository;
import com.site.pine.repository.shorts.ShortsPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MainService {

    private final PostRepository postRepository;
    private final GroupContentsRepository groupContentsRepository;
    private final MemberRepository memberRepository;
    private final CommunityPostRepository communityPostRepository;
    private final ShortsPostRepository shortsPostRepository;

    @Transactional(readOnly = true)
    public List<PostAllDto> getBestPost() {
        Pageable limitSix = PageRequest.of(0, 6);
        return postRepository.findAllBestPost(limitSix);
    }

    @Transactional(readOnly = true)
    public List<GroupContentsJpqlResDto> getBestGroup() {
        Pageable limitSix = PageRequest.of(0, 12);
        return groupContentsRepository.findGroupBestResDto(limitSix);
    }

    @Transactional(readOnly = true)
    public List<MemberWithPostsDto> getRandomMemberWithPosts() {
        List<MemberWithPostsDto> resultList = new ArrayList<>();

        // 1. 랜덤 멤버 4명 조회
        Pageable memberLimit = PageRequest.of(0, 4);
        Page<Member> members = memberRepository.findRandomMembers(memberLimit); // 추후 랜덤 쿼리로 변경 권장

        // 2. 각 멤버별로 글 2개씩 가져와서 DTO에 담기
        Pageable postLimit = PageRequest.of(0, 2);

        for (Member member : members.getContent()) {
            // 2-1. 부모 DTO 생성 (멤버 정보)
            MemberWithPostsDto memberDto = new MemberWithPostsDto(member);

            // 2-2. 해당 멤버의 글 조회 (기존 Repository 메서드 재사용)
            List<MemberAndCPostResDto> posts = communityPostRepository.findRandomByMemberId(member.getId(), postLimit);

            // 2-3. 조회된 글을 자식 DTO로 변환하여 추가
            for (MemberAndCPostResDto post : posts) {
                memberDto.addPost(new MemberPostItemDto(
                        post.getPostId(),
                        post.getCategory(),
                        post.getPostContent(),
                        post.getFileSrc()
                ));
            }

            // 2-4. 글이 하나라도 있으면 결과 리스트에 추가 (글 없는 멤버 제외 시)
            if (!memberDto.getPosts().isEmpty()) {
                resultList.add(memberDto);
            }
        }

        return resultList; // 최종 구조: [ 멤버A[글1, 글2], 멤버B[글3, 글4]... ]
    }

    @Transactional(readOnly = true)
    public List<ShortsBestDto> getBestShorts() {
        Pageable limitTen = PageRequest.of(0, 10);
        return shortsPostRepository.findBestShorts(limitTen);
    }
}
