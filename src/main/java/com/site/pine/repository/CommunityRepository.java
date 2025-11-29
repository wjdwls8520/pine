package com.site.pine.repository;

import com.site.pine.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByWriteDateDesc();
}
