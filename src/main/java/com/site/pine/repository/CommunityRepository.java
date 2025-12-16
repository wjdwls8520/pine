package com.site.pine.repository;

import com.site.pine.dto.community.PostListDto;
import com.site.pine.dto.community.PostMainFileDto;
import com.site.pine.dto.community.PostMainListDto;
import com.site.pine.entity.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommunityRepository extends JpaRepository<Post, Long> {

    @Query(
            value = """
        select new com.site.pine.dto.community.PostMainListDto(
            p.id,
            p.content,
            p.category,
            p.likeCount,
            p.replyCount,
            p.writeDate,
            m.id,
            m.nickname,
            m.profile_img
        )
        from Post p
        left join p.member m
        order by p.writeDate desc
    """,
            countQuery = """
        select count(p)
        from Post p
    """
    )
    Page<PostMainListDto> findMainPostList(Pageable pageable);



    @Query("""
    select new com.site.pine.dto.community.PostMainFileDto(
        f.post.id,
        f.id,
        f.path
    )
    from File f
    where f.post.id in :postIds
    """)
    List<PostMainFileDto> findFilesByPostIds(List<Long> postIds);


    @EntityGraph(attributePaths = {"files"})
    Page<Post> findAllByOrderByWriteDateDesc(Pageable pageable);


    Optional<Post> findById(Long id);

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
