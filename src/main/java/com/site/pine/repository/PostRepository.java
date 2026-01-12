package com.site.pine.repository;

import com.site.pine.dto.community.PostListDto;
import com.site.pine.dto.community.CommunityListDto;
import com.site.pine.dto.group.GroupPostListDto;
import com.site.pine.dto.post.PostAllDto;
import com.site.pine.dto.shorts.ShortsMainDto;
import com.site.pine.entity.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query(
            value = """
        select new com.site.pine.dto.community.CommunityListDto(
            p.id,
            p.content,
            cp.category,
            p.likeCount,
            p.replyCount,
            p.writeDate,
            m.id,
            m.nickname,
            m.profile_img
        )
        from CommunityPost cp
        join cp.post p
        left join p.member m 
        WHERE (:category IS NULL OR cp.category = :category)
        order by p.writeDate desc
    """,

            countQuery = """
        select count(cp)
        from CommunityPost cp
        WHERE (:category IS NULL OR cp.category = :category)
    """
    )
    Page<CommunityListDto> getAllCommunityPostList(
            Pageable pageable,
            @Param("category") Integer category // ⬅️ 파라미터 추가
    );

    @Query(
            value = """
        select new com.site.pine.dto.shorts.ShortsMainDto(
            p.id,
            sp.title,
            p.content,
            p.writeDate,
            p.updateDate,
            m.id,
            m.nickname,
            m.profile_img,                
            p.replyCount,
            p.likeCount,
            (CASE WHEN EXISTS (
                SELECT 1 FROM PostLike pl
                WHERE pl.post = p AND pl.member.id = :loginId
            ) THEN true ELSE false END),
            sp.viewCount
        )
        from ShortsPost sp
        join sp.post p
        left join p.member m
        where exists (
            select 1 from File f
            where f.post = p
            and f.status = 2
        )
        order by p.writeDate desc, p.id desc
    """,
            countQuery = """
        select count(sp)
        from ShortsPost sp
        join sp.post p
        where exists (
            select 1 from File f
            where f.post = p
            and f.status = 2
        )
    """
    )
    Page<ShortsMainDto> getAllShortsPostList(@Param("loginId") Long loginId, Pageable pageable);

    @Query(
            value = """
        select new com.site.pine.dto.group.GroupPostListDto(
            p.id,
            p.content,
            gp.groupContents.id,
            p.likeCount,
            p.replyCount,
            p.writeDate,
            m.id,
            m.nickname,
            m.profile_img
        )
        from GroupPost gp
        join gp.post p
        left join p.member m 
        where gp.groupContents.id = :groupId
        order by  p.writeDate desc
    """,

            countQuery = """
        select count(gp)
        from GroupPost gp
        join gp.post p
        where gp.groupContents.id = :groupId
    """
    )
    Page<GroupPostListDto> getAllGroupPostList(Pageable pageable, @Param("groupId") Long groupId);


    @Query("""
        select new com.site.pine.dto.community.PostListDto(
            p.id,
            p.content
        )
        from Post p
        where p.status = 1
        order by p.writeDate desc
    """)
    List<PostListDto> findPostList();


    // 포스트 likeCount 공통사용
    // likeCount 증가
    // save()된 내용이 먼저 DB에 반영된 후 -> update 쿼리 실행 -> clear
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Post p SET p.likeCount = p.likeCount + 1 WHERE p.id = :postId")
    void increaseLikeCount(@Param("postId") Long postId);

    // [수정] flushAutomatically = true 추가
    // delete()된 내용이 먼저 DB에 반영된 후 -> update 쿼리 실행 -> clear
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Post p SET p.likeCount = (CASE WHEN p.likeCount > 0 THEN p.likeCount - 1 ELSE 0 END) WHERE p.id = :postId")
    void decreaseLikeCount(@Param("postId") Long postId);

    // 업데이트 후 최신 카운트 조회용 (Entity 전체 조회보다 가벼움)
    @Query("SELECT p.likeCount FROM Post p WHERE p.id = :postId")
    Integer findLikeCountById(@Param("postId") Long postId);


    // 1. 댓글 수 증가(동시성 보장)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Post p SET p.replyCount = p.replyCount + 1 WHERE p.id = :postId")
    void increaseReplyCount(@Param("postId") Long postId);

    // 2. 댓글 수 감소 (0 이하로 내려가지 않게 방어 로직 포함)(동시성 보장)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Post p SET p.replyCount = (CASE WHEN p.replyCount > 0 THEN p.replyCount - 1 ELSE 0 END) WHERE p.id = :postId")
    void decreaseReplyCount(@Param("postId") Long postId);



    // 일반 포스트와 그룹 포스트를 좋아요 순으로 조회
    @Query(value = """
        SELECT new com.site.pine.dto.post.PostAllDto(
            p.id,
            m.id,
            m.nickname,
            m.profile_img,
            p.content,
            p.replyCount,
            p.likeCount,
            (SELECT f.path FROM File f WHERE f.post = p ORDER BY f.id ASC LIMIT 1)
        )
        from CommunityPost cp
        join cp.post p
        join p.member m
        order by p.likeCount desc, p.writeDate desc
    """
    )
    List<PostAllDto> findAllBestPost(Pageable pageable);
}
