package com.site.pine.repository.group;

import com.site.pine.entity.group.GroupCategoryList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupCategoryRepository extends JpaRepository<GroupCategoryList, Integer> {
}
