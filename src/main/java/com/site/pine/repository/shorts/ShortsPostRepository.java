package com.site.pine.repository.shorts;

import com.site.pine.entity.shorts.ShortsPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShortsPostRepository extends JpaRepository<ShortsPost, Long> {

    /**
     * [조회수 증가 - Atomic Update]
     * Java 메모리가 아닌 DB에서 직접 +1 연산을 수행하여 동시성 문제 해결
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ShortsPost s SET s.viewCount = s.viewCount + 1 WHERE s.postId = :id")
    void increaseViewCount(@Param("id") Long id);
}
