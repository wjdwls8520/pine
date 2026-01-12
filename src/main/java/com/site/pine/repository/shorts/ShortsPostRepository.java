package com.site.pine.repository.shorts;

import com.site.pine.entity.shorts.ShortsPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShortsPostRepository extends JpaRepository<ShortsPost, Long> {

    // 쇼츠 재생수 증가
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ShortsPost s SET s.viewCount = s.viewCount + 1 WHERE s.postId = :id")
    void increaseViewCount(@Param("id") Long id);
}
