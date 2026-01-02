package com.site.pine.repository.group;

import com.site.pine.dto.group.GroupContentsJpqlResDto;
import com.site.pine.entity.group.GroupContents;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface GroupContentsRepository extends JpaRepository<GroupContents, Long> {
    @Query(
            value = """
            select new com.site.pine.dto.group.GroupContentsJpqlResDto(
                gc.id,
                gc.groupName,
                gc.groupDescription,
                gc.joinState,
                gc.autoJoin,
                gc.userLimit,
                gc.allViewCount,
                gc.likeCount,
                gc.postCount,
                gc.groupMemberCount,
                gc.todayViewCount,
                gc.indate,
                f.id,
                f.path
            )
            from GroupContents gc
            left join gc.file f
            order by gc.indate desc
        """,
        countQuery = """
            select count(gc)
            from GroupContents gc
        """
    )
    Page<GroupContentsJpqlResDto> findGroupResDto(Pageable pageable);

    @Query(
        value = """
            select new com.site.pine.dto.group.GroupContentsJpqlResDto(
                gc.id,
                gc.groupName,
                gc.groupDescription,
                gc.joinState,
                gc.autoJoin,
                gc.userLimit,
                gc.allViewCount,
                gc.likeCount,
                gc.postCount,
                gc.groupMemberCount,
                gc.todayViewCount,
                gc.indate,
                f.id,
                f.path
            )
            from GroupContents gc
            left join gc.file f
            where exists (
                select 1
                from GroupInCategory c
                where c.groupContents = gc
                  and c.categoryId = :categoryId
            )
            order by gc.indate desc
        """,
        countQuery = """
            select count(gc)
            from GroupContents gc
            where exists (
                select 1
                from GroupInCategory c
                where c.groupContents = gc
                  and c.categoryId = :categoryId
            )
        """
    )
    Page<GroupContentsJpqlResDto> findGroupResDto(Integer categoryId, Pageable pageable);

    @Modifying
    @Query("update GroupContents g set g.allViewCount = g.allViewCount + 1 where g.id = :id")
    void increaseViewCount(@Param("id") Long groupId);

    @Modifying
    @Query("update GroupContents g set g.todayViewCount = g.todayViewCount + 1 where g.id = :id")
    void increaseTodayViewCount(@Param("id") Long groupId);

}
