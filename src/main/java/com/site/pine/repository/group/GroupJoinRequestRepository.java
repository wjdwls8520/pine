package com.site.pine.repository.group;

import com.site.pine.dto.group.GroupJoinResDto;
import com.site.pine.entity.Member;
import com.site.pine.entity.group.GroupContents;
import com.site.pine.entity.group.GroupJoinRequest;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GroupJoinRequestRepository extends JpaRepository<GroupJoinRequest, Long> {

    Boolean existsByGroupContentsAndMemberAndStatus(GroupContents groupE, Member memberE, int i);

    @Query(value = """
        select new com.site.pine.dto.group.GroupJoinResDto(
             gjr.id,
             gc.id,
             gc.groupName,
             m.id,
             m.email,
             m.nickname,
             m.profile_img,
             gjr.introduction,
             gjr.status,
             gjr.requestDate
        )
        from GroupJoinRequest gjr
        join gjr.groupContents gc
        join gjr.member m
        where gc.id = :groupId
        """,
        countQuery = """
            select count(gjr)
            from GroupJoinRequest gjr
            where gjr.groupContents.id = :groupId
        """
    )
    Page<GroupJoinResDto> findAllJoinGroupAndMember(Pageable pageable, @Param("groupId") Long groupId);

    @Modifying
    @Query("DELETE FROM GroupJoinRequest gjr WHERE gjr.groupContents.id = :groupId")
    void deleteAllByGroupId(@Param("id") Long groupId);

    Boolean existsByGroupContentsIdAndMemberIdAndStatus(Long groupId, Long memberId, int i);
}
