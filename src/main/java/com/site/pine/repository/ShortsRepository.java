package com.site.pine.repository;

import com.site.pine.entity.post.Post;
import com.site.pine.entity.shorts.Shorts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortsRepository extends JpaRepository<Shorts, Long> {
    @EntityGraph(attributePaths = {"files"})
    Page<Shorts> findAllByOrderByIndateDesc(Pageable pageable);
}
