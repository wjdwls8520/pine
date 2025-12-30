package com.site.pine.repository;

import com.site.pine.entity.Member;
import com.site.pine.entity.ViewGroupHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ViewGroupRepository extends JpaRepository<ViewGroupHistory, Long> {

    boolean existsByTargetIdAndViewerAndIsView(Long groupId, Member memberE, LocalDate today);

    boolean existsByTargetIdAndViewerCookieAndIsView(Long groupId, String viewerCookie, LocalDate today);

    Long countByTargetIdAndIsView(Long groupId, LocalDate today);
}
