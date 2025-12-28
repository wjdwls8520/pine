package com.site.pine.service;

import com.site.pine.dto.group.ViewCompareResult;
import com.site.pine.entity.Member;
import com.site.pine.entity.ViewHistory;
import com.site.pine.enums.PageType;
import com.site.pine.repository.MemberRepository;
import com.site.pine.repository.ViewRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class ViewService {

    private final MemberRepository mr;
    private final ViewRepository vr;

    @PersistenceContext
    private EntityManager entityManager;

    private final GroupService gs;
    private final CommunityService cs;
    private final ShortsService ss;

    @Transactional(noRollbackFor = DataIntegrityViolationException.class)
    public HashMap<String, Object> addViewCount(Long targetId, PageType pageType, Long isMember, String viewerCookie) {

        Member memberE = (isMember != null) ? mr.getReferenceById(isMember) : null;

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        boolean exists = (isMember != null)
                ? vr.existsByTargetTypeAndTargetIdAndViewerAndIsView(pageType, targetId, memberE, today)
                : vr.existsByTargetTypeAndTargetIdAndViewerCookieAndIsView(pageType, targetId, viewerCookie, today);

        if (!exists) {
            try {
                ViewHistory vh = ViewHistory.create(
                        pageType, targetId, memberE, viewerCookie, today
                );
                vr.save(vh);

                return switch (pageType) {
                    case GROUP -> gs.addViewCount(targetId, today);
//                    case COMMUNITY -> cs.addViewCount(targetId);
//                    case SHORTS -> ss.addViewCount(targetId);
                    default -> throw new IllegalStateException("지원하지 않는 페이지 타입입니다.");
                };

            } catch (DataIntegrityViolationException e) {
                // race condition 패배 → 정상 흐름
            }
        }
        return switch (pageType) {
            case GROUP -> gs.getViewCounts(targetId);
//            case COMMUNITY -> cs.getViewCounts(targetId);
//            case SHORTS -> ss.getViewCounts(targetId);
            default -> throw new IllegalStateException("지원하지 않는 페이지 타입입니다.");
        };
    }


    @Transactional(readOnly = true)
    public ViewCompareResult calculateCompare(Long targetId) {
        Long todayCount = vr.countByTargetTypeAndTargetIdAndIsView(
                PageType.GROUP,
                targetId,
                LocalDate.now(ZoneId.of("Asia/Seoul"))
        );

        Long yesterdayCount = vr.countByTargetTypeAndTargetIdAndIsView(
                PageType.GROUP,
                targetId,
                LocalDate.now().minusDays(1)
        );

        if (yesterdayCount == 0) {
            if (todayCount == 0) {
                return new ViewCompareResult(0, 0, "SAME");
            }
            return new ViewCompareResult(todayCount, 100, "NEW");
        }

        double percent = ((double)(todayCount - yesterdayCount) / yesterdayCount) * 100;

        return new ViewCompareResult(
                todayCount,
                Math.round(percent * 10) / 10.0, // 소수 1자리
                percent > 0 ? "UP" : percent < 0 ? "DOWN" : "SAME"
        );
    }
}
