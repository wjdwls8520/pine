package com.site.pine.entity.like;

import com.site.pine.entity.Member;
import com.site.pine.entity.Reply;
import com.site.pine.entity.group.GroupContents;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "group_like", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_group_like_member_reply",
                columnNames = {"member_id", "group_id"}
        )
})
public class GroupLike extends BaseLike {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private GroupContents groupContents;

    public GroupLike(GroupContents groupContents, Member member) {
        this.groupContents = groupContents;
        super.setMember(member);
    }

}
