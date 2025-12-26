package com.site.pine.repository;

import com.site.pine.entity.Member;
import com.site.pine.entity.ViewHistory;
import com.site.pine.enums.PageType;
import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ViewRepository extends JpaRepository<ViewHistory, Long> {

    boolean existsByTargetTypeAndTargetIdAndViewerAndIsView(PageType pageType, Long groupId, Member memberE, LocalDate today);

    boolean existsByTargetTypeAndTargetIdAndViewerCookieAndIsView(PageType pageType, Long groupId, String viewerCookie, LocalDate today);

    Long countByTargetTypeAndTargetIdAndIsView(PageType pageType, Long groupId, LocalDate today);
}
