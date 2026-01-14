package com.site.pine.repository.shorts;

import com.site.pine.dto.shorts.ShortsBestDto;
import com.site.pine.entity.shorts.ShortsPost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShortsPostRepository extends JpaRepository<ShortsPost, Long> {

    // 쇼츠 재생수 증가
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ShortsPost s SET s.viewCount = s.viewCount + 1 WHERE s.postId = :id")
    void increaseViewCount(@Param("id") Long id);

    // 인기 쇼츠 조회 (조회수와 좋아요 기준)
    @Query("""
        select new com.site.pine.dto.shorts.ShortsBestDto(
            sp.postId,
            sp.title,
            m.nickname,
            (SELECT f.path FROM File f WHERE f.post = sp.post AND f.contentType LIKE 'image/%' AND f.status = 2 ORDER BY f.id ASC LIMIT 1)
        )
        from ShortsPost sp
        join sp.post p
        left join p.member m
        where exists (
            select 1 from File f
            where f.post = p
            and f.status = 2
            and f.contentType LIKE 'image/%'
        )
        order by sp.viewCount desc, p.likeCount desc, p.writeDate desc
    """)
    List<ShortsBestDto> findBestShorts(Pageable pageable);
}
