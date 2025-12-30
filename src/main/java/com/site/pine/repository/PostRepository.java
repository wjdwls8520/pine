package com.site.pine.repository;

import com.site.pine.dto.community.PostListDto;
import com.site.pine.dto.community.CommunityListDto;
import com.site.pine.dto.shorts.ShortsMainDto;
import com.site.pine.entity.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
        order by  p.writeDate desc
    """,

    countQuery = """
        select count(cp)
        from CommunityPost cp
    """
    )
    Page<CommunityListDto> getAllCommunityPostList(Pageable pageable);

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
            m.profile_img
        )
        from ShortsPost sp
        join sp.post p
        left join p.member m
        order by p.writeDate desc, p.id desc
    """,
            countQuery = """
        select count(sp)
        from ShortsPost sp
    """
    )
    Page<ShortsMainDto> getAllShortsPostList(Pageable pageable);



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


}
