package com.site.pine.repository;

import com.site.pine.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {

    Optional<Likes> findByMember_IdAndTargetTypeAndTargetId(
            Long memberId,
            int targetType,
            Long targetId
    );

    long countByTargetTypeAndTargetId(int targetType, Long targetId);

    boolean existsByMember_IdAndTargetTypeAndTargetId(Long memberId, int i, Long id);
}
