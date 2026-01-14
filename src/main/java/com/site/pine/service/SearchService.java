package com.site.pine.service;

import com.site.pine.dto.search.SearchGroupDto;
import com.site.pine.dto.search.SearchMemberDto;
import com.site.pine.dto.search.SearchPostDto;
import com.site.pine.entity.community.CommunityPost;
import com.site.pine.entity.group.GroupPost;
import com.site.pine.entity.shorts.ShortsPost;
import com.site.pine.repository.MemberRepository;
import com.site.pine.repository.group.GroupContentsRepository;
import com.site.pine.repository.search.PostSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final PostSearchRepository postSearchRepository;
    private final GroupContentsRepository groupContentsRepository;
    private final MemberRepository memberRepository;

    public Map<String, Object> searchByType(String type, String keyword, int page, String sort, String period, Long memberId, Long groupId) {
        Map<String, Object> result = new HashMap<>();
        Pageable pageable = PageRequest.of(page, 10);

        switch (type) {
            case "COMMUNITY":
                // 커뮤니티는 memberId, groupId가 필요 없음 -> null, null 전달
                List<SearchPostDto> commuList = postSearchRepository.searchPosts(CommunityPost.class, keyword, pageable, sort, period, null, null);
                result.put("list", commuList);
                result.put("isLast", commuList.size() < 10);
                break;

            case "GROUP_POST":
                // [중요] 그룹 포스트는 memberId(권한체크), groupId(필터링) 모두 전달
                List<SearchPostDto> groupPostList = postSearchRepository.searchPosts(
                        GroupPost.class, keyword, pageable, sort, period, memberId, groupId
                );
                result.put("list", groupPostList);
                result.put("isLast", groupPostList.size() < 10);
                break;

            case "SHORTS":
                // 쇼츠는 memberId, groupId 필요 없음 -> null, null 전달
                List<SearchPostDto> shortsList = postSearchRepository.searchPosts(ShortsPost.class, keyword, pageable, sort, period, null, null);
                result.put("list", shortsList);
                result.put("isLast", shortsList.size() < 10);
                break;

            case "GROUP":
                List<SearchGroupDto> groupList = groupContentsRepository.searchGroups(keyword, pageable, sort);
                result.put("list", groupList);
                result.put("isLast", groupList.size() < 10);
                break;

            case "MEMBER":
                List<SearchMemberDto> memberList = memberRepository.searchMembers(keyword, pageable);
                result.put("list", memberList);
                result.put("isLast", memberList.size() < 10);
                break;
        }
        return result;
    }
}