package com.site.pine.repository;

import com.site.pine.dto.tag.TagResDto;
import com.site.pine.entity.TagMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagMappingRepository extends JpaRepository<TagMapping, Long> {
    // 1. 단순 조회 (TagService용)
    List<TagMapping> findAllByTargetId(Long targetId);

    // 2. 삭제 (TagService용)
    void deleteByTargetId(Long targetId);

    // 여러 게시글의 태그를 한 번에 가져오는 쿼리 (CommunityService 조회용)
    // TagResDto 생성자가 (tagId, tagName, targetId) 순서라고 가정합니다.
    @Query("select new com.site.pine.dto.tag.TagResDto(t.id, t.name, tm.targetId) " +
            "from TagMapping tm " +
            "join tm.tag t " +
            "where tm.targetId in :targetIds")
    List<TagResDto> findTagsByTargetIds(@Param("targetIds") List<Long> targetIds);
}
