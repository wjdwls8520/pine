package com.site.pine.repository.group;

import com.site.pine.entity.group.GroupPost;
import com.site.pine.entity.post.Post;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GroupPostRepository extends JpaRepository<GroupPost, Long> {
    // [추가] 수정 페이지용: CommunityPost + Post 같이 가져오기 (성능 최적화)
    @Query("SELECT cp FROM GroupPost cp JOIN FETCH cp.post WHERE cp.postId = :id")
    Optional<GroupPost> findByIdWithPost(@Param("id") Long id);

    void deleteByPostId(Long postId);

    @Query(value = "SELECT gp FROM GroupPost gp JOIN FETCH gp.post p JOIN FETCH p.member m WHERE gp.groupContents.id = :groupId order by p.writeDate",
            countQuery = "SELECT count(gp) FROM GroupPost gp WHERE gp.groupContents.id = :groupId")
    Page<GroupPost> findByGroupPost(@Param("groupId") Long groupId, Pageable pageable);
}
