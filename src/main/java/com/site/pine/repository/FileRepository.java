package com.site.pine.repository;

import com.site.pine.dto.community.PostMainFileDto;
import com.site.pine.dto.shorts.ShortsFileDto;
import com.site.pine.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {

    @Query("""
        select new com.site.pine.dto.shorts.ShortsFileDto(
            f.id,
            f.shorts.id,
            f.pageType,
            f.path,
            f.contentType
        )
        from File f
        where f.shorts.id in :shortsIds
    """)
    List<ShortsFileDto> findFilesByShortsIds(List<Long> shortsIds);
}
