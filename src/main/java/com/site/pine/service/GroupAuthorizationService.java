package com.site.pine.service;

import com.site.pine.entity.Member;
import com.site.pine.entity.group.GroupContents;
import com.site.pine.entity.group.GroupMember;
import com.site.pine.repository.MemberRepository;
import com.site.pine.repository.group.GroupContentsRepository;
import com.site.pine.repository.group.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 읽기 전용 트랜잭션 (성능 최적화 필수)
public class GroupAuthorizationService {

    private final MemberRepository memberRepository;
    private final GroupContentsRepository groupContentsRepository;
    private final GroupMemberRepository groupMemberRepository;

    // 1. 로그인 여부 검증 (이건 DB 조회가 아니므로 기존 유지)
    public void validateLoginMember() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("로그인이 필요한 서비스입니다.");
        }
    }

    // 2. 멤버 조회 (검증 + 객체 반환)
    public Member getMemberOrThrow(Long memberId) {
        if (memberId == null) throw new IllegalArgumentException("멤버 ID 값이 없습니다.");

        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    // 3. 그룹 조회 (검증 + 객체 반환)
    public GroupContents getGroupOrThrow(Long groupId) {
        if (groupId == null) throw new IllegalArgumentException("그룹 ID 값이 없습니다.");

        return groupContentsRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 그룹입니다."));
    }

    // 4. 그룹 멤버인지 조회 (검증 + 객체 반환)
    public GroupMember getGroupMemberOrThrow(Long groupId, Long memberId) {
        // GroupMember 테이블에 데이터가 있다는 건, 이미 그룹과 멤버가 존재한다는 뜻(FK)입니다.
        return groupMemberRepository.findByGroupContentsIdAndMemberId(groupId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹의 멤버가 아닙니다."));
    }
}