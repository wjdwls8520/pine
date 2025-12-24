package com.site.pine.repository;

import com.site.pine.entity.Member;
import com.site.pine.entity.ViewHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ViewRepository extends JpaRepository<ViewHistory, Long> {

    boolean existsByTargetTypeAndTargetIdAndViewerAndIsView(int i, Long groupId, Member memberE, LocalDate today);

    boolean existsByTargetTypeAndTargetIdAndViewerCookieAndIsView(int i, Long groupId, String viewerCookie, LocalDate today);
}
