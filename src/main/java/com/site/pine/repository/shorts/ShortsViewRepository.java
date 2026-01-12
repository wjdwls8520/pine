package com.site.pine.repository.shorts;

import com.site.pine.entity.shorts.ShortsViewHistory; // 변경된 이름 import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ShortsViewRepository extends JpaRepository<ShortsViewHistory, Long> {

    /**
     * [로그인 유저용]
     * 특정 회원(memberId)이 특정 쇼츠(shortsId)를
     * 기준 시간(timeLimit) 이후에 조회한 기록이 있는지 확인 (COUNT > 0)
     */
    @Query("SELECT COUNT(h) > 0 FROM ShortsViewHistory h " +
            "WHERE h.targetId.postId = :shortsId " +
            "AND h.viewer.id = :memberId " +
            "AND h.viewedAt > :timeLimit")
    boolean existsByMemberRecent(@Param("shortsId") Long shortsId,
                                 @Param("memberId") Long memberId,
                                 @Param("timeLimit") LocalDateTime timeLimit);

    /**
     * [비회원 유저용]
     * 특정 쿠키(cookie)를 가진 사용자가 특정 쇼츠(shortsId)를
     * 기준 시간(timeLimit) 이후에 조회한 기록이 있는지 확인
     */
    @Query("SELECT COUNT(h) > 0 FROM ShortsViewHistory h " +
            "WHERE h.targetId.postId = :shortsId " +
            "AND h.viewerCookie = :cookie " +
            "AND h.viewedAt > :timeLimit")
    boolean existsByCookieRecent(@Param("shortsId") Long shortsId,
                                 @Param("cookie") String cookie,
                                 @Param("timeLimit") LocalDateTime timeLimit);
}