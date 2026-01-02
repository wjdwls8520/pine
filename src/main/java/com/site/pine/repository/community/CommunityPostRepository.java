package com.site.pine.repository.community;

import com.site.pine.entity.community.CommunityPost;
import com.site.pine.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
    void deleteByPost(Post post);
}
