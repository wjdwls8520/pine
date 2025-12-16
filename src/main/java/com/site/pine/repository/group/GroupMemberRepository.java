package com.site.pine.repository.group;

import com.site.pine.dto.member.MemberDto;
import com.site.pine.entity.group.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    @Query("""
        select gm
        from GroupMember gm
        where gm.member.id = :memberId
        and gm.groupContents.id = :groupId
    """)
    Optional<GroupMember> findByMemberIdAndGroupId(Long memberId, Long groupId);
}
