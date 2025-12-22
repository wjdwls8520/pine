package com.site.pine.repository;

import com.site.pine.dto.tag.TagResDto;
import com.site.pine.entity.TagMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagMappingRepository extends JpaRepository<TagMapping,Long> {

    @Query("""
        select new com.site.pine.dto.tag.TagResDto(
            t.id,
            t.name,
            tm.targetId
        )
        from TagMapping tm
        join tm.tag t
        where tm.targetType = :targetType
          and tm.targetId in :targetIds
    """)
    List<TagResDto> findTagsByTargetIds(
            @Param("targetType") int targetType,
            @Param("targetIds") List<Long> targetIds
    );
}
