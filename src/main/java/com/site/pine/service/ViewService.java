package com.site.pine.service;

import com.site.pine.dto.group.ViewCompareResult;
import com.site.pine.entity.Member;
import com.site.pine.entity.ViewGroupHistory;
import com.site.pine.repository.MemberRepository;
import com.site.pine.repository.ViewGroupRepository;
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
    private final ViewGroupRepository vgr;
    private final GroupService gs;

    @Transactional(noRollbackFor = DataIntegrityViolationException.class)
    public HashMap<String, Object> addGroupViewCount(Long targetId, Long isMember, String viewerCookie) {

        Member memberE = (isMember != null) ? mr.getReferenceById(isMember) : null;

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        boolean exists = (isMember != null)
                ? vgr.existsByTargetIdAndViewerAndIsView(targetId, memberE, today)
                : vgr.existsByTargetIdAndViewerCookieAndIsView(targetId, viewerCookie, today);

        if (!exists) {
            try {
                ViewGroupHistory vh = ViewGroupHistory.create(
                        targetId, memberE, viewerCookie, today
                );
                vgr.save(vh);
                // 그룹 뷰 중가.
                gs.addViewCount(targetId, today);


            } catch (DataIntegrityViolationException e) {
                // race condition 패배 → 정상 흐름
            }
        }

        return gs.getViewCounts(targetId);

    }


    @Transactional(readOnly = true)
    public ViewCompareResult groupCalculateCompare(Long targetId) {
        Long todayCount = vgr.countByTargetIdAndIsView(
                targetId,
                LocalDate.now(ZoneId.of("Asia/Seoul"))
        );

        Long yesterdayCount = vgr.countByTargetIdAndIsView(
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
