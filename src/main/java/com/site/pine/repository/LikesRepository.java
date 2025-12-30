package com.site.pine.repository;

import com.site.pine.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {

    Optional<Likes> findByMember_IdAndTargetId(
            Long memberId,
            Long targetId
    );

    boolean existsByMember_IdAndTargetId(Long memberId, Long id);
}
