package com.site.pine.repository;

import com.site.pine.entity.Likes;
import com.site.pine.enums.PageType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {

    Optional<Likes> findByMember_IdAndTargetTypeAndTargetId(
            Long memberId,
            PageType targetType,
            Long targetId
    );

    long countByTargetTypeAndTargetId(PageType targetType, Long targetId);

    boolean existsByMember_IdAndTargetTypeAndTargetId(Long memberId, PageType targetType, Long id);
}
