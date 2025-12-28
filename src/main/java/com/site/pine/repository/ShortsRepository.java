package com.site.pine.repository;

import com.site.pine.dto.shorts.ShortsMainDto;
import com.site.pine.entity.post.Post;
import com.site.pine.entity.shorts.Shorts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShortsRepository extends JpaRepository<Shorts, Long> {

//    @EntityGraph(attributePaths = {"files"})
//    Page<Shorts> findAllByOrderByIndateDescIdDesc(Pageable pageable);

    @Query(
            value = """
        select new com.site.pine.dto.shorts.ShortsMainDto(
            s.id,
            s.title,
            s.content,
            s.indate,
            s.updateDate,
            m.id,
            m.nickname,
            m.profile_img
        )
        from Shorts s
        join s.member m
        order by s.indate desc, s.id desc
    """,
            countQuery = """
        select count(s)
        from Shorts s
    """
    )
    Page<ShortsMainDto> findMainShortsList(Pageable pageable);


}
