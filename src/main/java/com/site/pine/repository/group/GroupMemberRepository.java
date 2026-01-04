package com.site.pine.repository.group;

import com.site.pine.dto.member.MemberDto;
import com.site.pine.entity.Member;
import com.site.pine.entity.group.GroupContents;
import com.site.pine.entity.group.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    @Query("""
        select gm
        from GroupMember gm
        where gm.member.id = :memberId
        and gm.groupContents.id = :groupId
    """)
    Optional<GroupMember> findByMemberIdAndGroupId(Long memberId, Long groupId);


    Optional<GroupMember> findByMemberAndGroupContents(Member memberE, GroupContents groupContentE);

    @Modifying
    @Query("DELETE FROM GroupMember gm WHERE gm.groupContents.id = :groupId")
    void deleteAllByGroupId(@Param("groupId") Long groupId);

    Boolean existsByGroupContentsAndMember(GroupContents groupE, Member memberE);

    Optional<GroupMember> findByGroupContentsIdAndMemberId(Long groupId, Long memberId);

    boolean existsByGroupContentsIdAndMemberId(Long groupId, Long id);
}
