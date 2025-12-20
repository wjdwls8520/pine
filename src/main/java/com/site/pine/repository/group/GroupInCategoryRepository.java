package com.site.pine.repository.group;

import com.site.pine.entity.group.GroupContents;
import com.site.pine.entity.group.GroupInCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupInCategoryRepository extends JpaRepository<GroupInCategory, Long> {

    void deleteByGroupContents(GroupContents groupContentsE);
}
