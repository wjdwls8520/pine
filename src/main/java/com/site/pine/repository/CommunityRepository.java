package com.site.pine.repository;

import com.site.pine.entity.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = {"files"})
    Page<Post> findAllByOrderByWriteDateDesc(Pageable pageable);

}
