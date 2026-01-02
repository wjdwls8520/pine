package com.site.pine.repository.group;

import com.site.pine.entity.group.GroupContents;
import com.site.pine.entity.group.GroupInCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupInCategoryRepository extends JpaRepository<GroupInCategory, Long> {

    @Query("SELECT gic FROM GroupInCategory gic JOIN FETCH gic.categoryId WHERE gic.groupContents.id = :groupId")
    List<GroupInCategory> findAllByGroupIdWithCategory(@Param("groupId") Long groupId);

    void deleteAllByGroupContents(GroupContents groupContents);
}
