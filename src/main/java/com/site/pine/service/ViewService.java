package com.site.pine.service;

import com.site.pine.dto.group.ViewCompareResult;
import com.site.pine.entity.Member;
import com.site.pine.entity.ViewHistory;
import com.site.pine.repository.MemberRepository;
import com.site.pine.repository.ViewRepository;
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
    private final GroupService gs;

    @Transactional(noRollbackFor = DataIntegrityViolationException.class)
    public HashMap<String, Object> addViewCount(Long targetId, Long isMember, String viewerCookie) {

        Member memberE = (isMember != null) ? mr.getReferenceById(isMember) : null;

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        boolean exists = (isMember != null)
                ? vr.existsByTargetIdAndViewerAndIsView(targetId, memberE, today)
                : vr.existsByTargetIdAndViewerCookieAndIsView(targetId, viewerCookie, today);

        if (!exists) {
            try {
                ViewHistory vh = ViewHistory.create(
                        targetId, memberE, viewerCookie, today
                );
                vr.save(vh);


                gs.addViewCount(targetId, today);


            } catch (DataIntegrityViolationException e) {
                // race condition 패배 → 정상 흐름
            }
        }

        return gs.getViewCounts(targetId);

    }


    @Transactional(readOnly = true)
    public ViewCompareResult calculateCompare(Long targetId) {
        Long todayCount = vr.countByTargetIdAndIsView(
                targetId,
                LocalDate.now(ZoneId.of("Asia/Seoul"))
        );

        Long yesterdayCount = vr.countByTargetIdAndIsView(
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
