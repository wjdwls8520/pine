package com.site.pine.repository.group;

import com.site.pine.entity.group.GroupContents;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GroupContentsRepository extends JpaRepository<GroupContents, Long> {

    Page<GroupContents> findAllByOrderByIndateDesc(Pageable pageable);
}
