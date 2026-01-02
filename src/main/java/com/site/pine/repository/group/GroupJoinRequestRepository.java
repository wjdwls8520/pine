package com.site.pine.repository.group;

import com.site.pine.entity.Member;
import com.site.pine.entity.group.GroupContents;
import com.site.pine.entity.group.GroupJoinRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupJoinRequestRepository extends JpaRepository<GroupJoinRequest, Long> {

    Boolean existsByGroupContentsAndMemberAndStatus(GroupContents groupE, Member memberE, int i);
}
