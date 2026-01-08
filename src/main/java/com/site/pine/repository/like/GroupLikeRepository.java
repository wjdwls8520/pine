package com.site.pine.repository.like;

import com.site.pine.entity.Member;
import com.site.pine.entity.group.GroupContents;
import com.site.pine.entity.like.GroupLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupLikeRepository extends JpaRepository<GroupLike, Long> {
    Optional<GroupLike> findByGroupContentsAndMember(GroupContents groupContents, Member member);

    Boolean existsByMemberIdAndGroupContentsId(Long id, Long groupId);
}
