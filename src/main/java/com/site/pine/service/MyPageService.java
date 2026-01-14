package com.site.pine.service;

import com.site.pine.dto.mypage.MyGroupListDto;
import com.site.pine.dto.mypage.MyPostListDto;
import com.site.pine.dto.post.PostMainFileDto;
import com.site.pine.entity.community.CommunityPost;
import com.site.pine.entity.group.GroupContents;
import com.site.pine.entity.group.GroupMember;
import com.site.pine.entity.group.GroupPost;
import com.site.pine.entity.post.Post;
import com.site.pine.entity.shorts.ShortsPost;
import com.site.pine.repository.FileRepository;
import com.site.pine.repository.ReplyRepository;
import com.site.pine.repository.community.CommunityPostRepository;
import com.site.pine.repository.group.GroupMemberRepository;
import com.site.pine.repository.group.GroupPostRepository;
import com.site.pine.repository.like.PostLikeRepository;
import com.site.pine.repository.shorts.ShortsPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final CommunityPostRepository communityPostRepository;
    private final GroupPostRepository groupPostRepository;
    private final ShortsPostRepository shortsPostRepository;
    private final ReplyRepository replyRepository;
    private final PostLikeRepository postLikeRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final FileRepository fileRepository;

    /**
     * 내가 작성한 게시글 조회
     */
    public Page<MyPostListDto> getMyPosts(Long memberId, Pageable pageable) {
        List<CommunityPost> communityPosts = communityPostRepository.findByMemberId(memberId, PageRequest.of(0, Integer.MAX_VALUE)).getContent();
        List<GroupPost> groupPosts = groupPostRepository.findByMemberId(memberId, PageRequest.of(0, Integer.MAX_VALUE)).getContent();
        List<ShortsPost> shortsPosts = shortsPostRepository.findByMemberId(memberId, PageRequest.of(0, Integer.MAX_VALUE)).getContent();

        List<Long> allPostIds = new java.util.ArrayList<>();
        communityPosts.forEach(cp -> allPostIds.add(cp.getPost().getId()));
        groupPosts.forEach(gp -> allPostIds.add(gp.getPost().getId()));
        shortsPosts.forEach(sp -> allPostIds.add(sp.getPostId()));

        Map<Long, String> thumbnailMap = getThumbnailMap(allPostIds);
        Map<Long, String> shortsTitleMap = shortsPosts.stream().collect(Collectors.toMap(ShortsPost::getPostId, ShortsPost::getTitle));
        Map<Long, Long> shortsViewCountMap = shortsPosts.stream().collect(Collectors.toMap(ShortsPost::getPostId, ShortsPost::getViewCount));

        List<MyPostListDto> allDtos = new java.util.ArrayList<>();

        // 1. Community
        communityPosts.forEach(cp -> {
            Post post = cp.getPost();
            String thumbnail = thumbnailMap.getOrDefault(post.getId(), null);
            String content = truncateContent(post.getContent(), 100);
            allDtos.add(MyPostListDto.builder()
                    .postId(post.getId())
                    .title(null)
                    .content(content)
                    .thumbnailImage(thumbnail)
                    .postType("COMMUNITY")
                    .viewCount(0L)
                    .likeCount(post.getLikeCount())
                    .replyCount(post.getReplyCount())
                    .writeDate(post.getWriteDate())
                    .build());
        });

        // 2. Group (🔥 여기가 비어있었습니다. 채워넣음)
        groupPosts.forEach(gp -> {
            Post post = gp.getPost();
            String thumbnail = thumbnailMap.getOrDefault(post.getId(), null);
            String content = truncateContent(post.getContent(), 100);

            // 🔥 그룹 ID 추출
            Long groupId = gp.getGroupContents() != null ? gp.getGroupContents().getId() : null;

            allDtos.add(MyPostListDto.builder()
                    .postId(post.getId())
                    .title(null)
                    .content(content)
                    .thumbnailImage(thumbnail)
                    .postType("GROUP")
                    .viewCount(0L)
                    .likeCount(post.getLikeCount())
                    .replyCount(post.getReplyCount())
                    .writeDate(post.getWriteDate())
                    .groupId(groupId) // 🔥 DTO에 주입
                    .build());
        });

        // 3. Shorts
        shortsPosts.forEach(sp -> {
            Post post = sp.getPost();
            String thumbnail = thumbnailMap.getOrDefault(post.getId(), null);
            String content = truncateContent(post.getContent(), 100);
            allDtos.add(MyPostListDto.builder()
                    .postId(post.getId())
                    .title(shortsTitleMap.getOrDefault(post.getId(), null))
                    .content(content)
                    .thumbnailImage(thumbnail)
                    .postType("SHORTS")
                    .viewCount(shortsViewCountMap.getOrDefault(post.getId(), 0L))
                    .likeCount(post.getLikeCount())
                    .replyCount(post.getReplyCount())
                    .writeDate(post.getWriteDate())
                    .build());
        });

        allDtos.sort((a, b) -> {
            if (a.getWriteDate() == null && b.getWriteDate() == null) return 0;
            if (a.getWriteDate() == null) return 1;
            if (b.getWriteDate() == null) return -1;
            return b.getWriteDate().compareTo(a.getWriteDate());
        });

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allDtos.size());
        List<MyPostListDto> pagedDtos = (start > end) ? new java.util.ArrayList<>() : allDtos.subList(start, end);

        return new PageImpl<>(pagedDtos, pageable, allDtos.size());
    }

    /**
     * 내가 댓글을 단 게시글 목록 조회 (🔥 수정됨)
     */
    public Page<MyPostListDto> getMyCommentedPosts(Long memberId, Pageable pageable) {
        Page<Post> posts = replyRepository.findPostsByMemberReplies(memberId, pageable);
        return convertToDtoWithGroupId(posts); // 🔥 공통 메서드로 변경하여 groupId 처리
    }

    /**
     * 내가 좋아요한 게시글 목록 조회 (🔥 수정됨)
     */
    public Page<MyPostListDto> getMyLikedPosts(Long memberId, Pageable pageable) {
        Page<Post> posts = postLikeRepository.findPostsByMemberLikes(memberId, pageable);
        return convertToDtoWithGroupId(posts); // 🔥 공통 메서드로 변경하여 groupId 처리
    }

    /**
     * 🔥 [핵심 추가] Post 리스트를 DTO로 변환하면서 GroupId를 같이 찾아주는 메서드
     */
    private Page<MyPostListDto> convertToDtoWithGroupId(Page<Post> posts) {
        List<Long> postIds = posts.getContent().stream().map(Post::getId).collect(Collectors.toList());

        Map<Long, String> thumbnailMap = getThumbnailMap(postIds);
        Map<Long, String> postTypeMap = getPostTypeMap(postIds);
        Map<Long, String> shortsTitleMap = getShortsTitleMap(postIds);
        Map<Long, Long> shortsViewCountMap = getShortsViewCountMap(postIds);

        // 🔥 여기가 핵심입니다. 게시글 ID로 그룹 ID를 찾아오는 맵을 만듭니다.
        Map<Long, Long> groupIdMap = getGroupIdMap(postIds);

        return posts.map(post -> {
            String postType = postTypeMap.getOrDefault(post.getId(), "COMMUNITY");
            String thumbnail = thumbnailMap.getOrDefault(post.getId(), null);
            String content = truncateContent(post.getContent(), 100);
            String title = shortsTitleMap.getOrDefault(post.getId(), null);
            Long viewCount = shortsViewCountMap.getOrDefault(post.getId(), 0L);

            // 🔥 맵에서 그룹 ID 꺼내기
            Long groupId = groupIdMap.getOrDefault(post.getId(), null);

            return MyPostListDto.builder()
                    .postId(post.getId())
                    .title(title)
                    .content(content)
                    .thumbnailImage(thumbnail)
                    .postType(postType)
                    .viewCount(viewCount)
                    .likeCount(post.getLikeCount())
                    .replyCount(post.getReplyCount())
                    .writeDate(post.getWriteDate())
                    .groupId(groupId) // 🔥 DTO에 주입 완료
                    .build();
        });
    }

    /**
     * 내가 가입한 그룹 목록 조회
     */
    public Page<MyGroupListDto> getMyGroups(Long memberId, Pageable pageable) {
        Page<GroupMember> groupMembers = groupMemberRepository.findAllByMemberId(memberId, pageable);

        return groupMembers.map(gm -> {
            GroupContents group = gm.getGroupContents();
            String role = convertRoleToString(gm.getRole());
            String groupImage = group.getFile() != null ? group.getFile().getPath() : null;

            return MyGroupListDto.builder()
                    .groupId(group.getId())
                    .groupName(group.getGroupName())
                    .groupImage(groupImage)
                    .totalMemberCount(group.getGroupMemberCount())
                    .myRole(role)
                    .build();
        });
    }

    /**
     * 🔥 [추가됨] 게시글 ID 목록으로 그룹 ID 매핑 테이블 생성
     */
    private Map<Long, Long> getGroupIdMap(List<Long> postIds) {
        if (postIds.isEmpty()) return Map.of();

        // GroupPostRepository에서 (post, groupContents) 페치 조인된 데이터를 가져옴
        List<GroupPost> groupPosts = groupPostRepository.findByPostIdIn(postIds);

        return groupPosts.stream()
                .collect(Collectors.toMap(
                        gp -> gp.getPost().getId(),       // Key: Post ID
                        gp -> gp.getGroupContents().getId(), // Value: Group ID
                        (existing, replacement) -> existing
                ));
    }

    // --- 기존 Helper Methods ---

    private Map<Long, String> getThumbnailMap(List<Long> postIds) {
        if (postIds.isEmpty()) return Map.of();
        List<PostMainFileDto> files = fileRepository.findFilesByPostIds(postIds);
        return files.stream()
                .filter(f -> f.getContentType() != null && f.getContentType().startsWith("image/"))
                .collect(Collectors.toMap(PostMainFileDto::getPostId, PostMainFileDto::getPath, (a, b) -> a));
    }

    private Map<Long, String> getPostTypeMap(List<Long> postIds) {
        if (postIds.isEmpty()) return Map.of();
        Map<Long, String> typeMap = new java.util.HashMap<>();

        List<GroupPost> groupPosts = groupPostRepository.findByPostIdIn(postIds);
        groupPosts.forEach(gp -> typeMap.put(gp.getPost().getId(), "GROUP")); // getPostId()가 아니라 getPost().getId()가 안전함

        List<ShortsPost> shortsPosts = shortsPostRepository.findByPostIdIn(postIds);
        shortsPosts.forEach(sp -> typeMap.put(sp.getPostId(), "SHORTS"));

        postIds.forEach(id -> typeMap.putIfAbsent(id, "COMMUNITY"));
        return typeMap;
    }

    private Map<Long, String> getShortsTitleMap(List<Long> postIds) {
        if (postIds.isEmpty()) return Map.of();
        List<ShortsPost> shortsPosts = shortsPostRepository.findByPostIdIn(postIds);
        return shortsPosts.stream().collect(Collectors.toMap(ShortsPost::getPostId, ShortsPost::getTitle));
    }

    private Map<Long, Long> getShortsViewCountMap(List<Long> postIds) {
        if (postIds.isEmpty()) return Map.of();
        List<ShortsPost> shortsPosts = shortsPostRepository.findByPostIdIn(postIds);
        return shortsPosts.stream().collect(Collectors.toMap(ShortsPost::getPostId, ShortsPost::getViewCount));
    }

    private String truncateContent(String content, int maxLength) {
        if (content == null) return null;
        if (content.length() <= maxLength) return content;
        return content.substring(0, maxLength) + "...";
    }

    private String convertRoleToString(Integer role) {
        if (role == null) return "MEMBER";
        return switch (role) {
            case 1 -> "MASTER";
            case 2 -> "MANAGER";
            default -> "MEMBER";
        };
    }
}