package com.site.pine.repository;

import com.site.pine.entity.Member;
import com.site.pine.entity.ViewGroupHistory;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface ViewGroupRepository extends JpaRepository<ViewGroupHistory, Long> {

    boolean existsByTargetId_IdAndViewerAndIsView(Long groupId, Member memberE, LocalDate today);

    boolean existsByTargetId_IdAndViewerCookieAndIsView(Long groupId, String viewerCookie, LocalDate today);

    Long countByTargetId_IdAndIsView(Long groupId, LocalDate today);

    @Modifying
    @Query("DELETE FROM ViewGroupHistory vgr WHERE vgr.targetId.id = :groupId")
    void deleteAllByGroupId(@Param("groupId") Long groupId);
}
