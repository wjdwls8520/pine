package com.site.pine.repository;

import com.site.pine.dto.community.PostListDto;
import com.site.pine.entity.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommunityRepository extends JpaRepository<Post, Long> {

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
