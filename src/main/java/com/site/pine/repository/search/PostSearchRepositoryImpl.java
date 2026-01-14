package com.site.pine.repository.search;

import com.site.pine.dto.search.SearchPostDto;
import com.site.pine.entity.group.GroupPost;
import com.site.pine.entity.shorts.ShortsPost;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostSearchRepositoryImpl implements PostSearchRepository {

    private final EntityManager em;

    @Override
    public <T> List<SearchPostDto> searchPosts(Class<T> entityType, String keyword, Pageable pageable, String sort, String period, Long memberId, Long groupId) {

        String entityName = entityType.getSimpleName();
        boolean isShorts = entityType.equals(ShortsPost.class);
        boolean isGroupPost = entityType.equals(GroupPost.class);

        StringBuilder jpql = new StringBuilder();

        // 1. SELECT 절
        jpql.append("SELECT new com.site.pine.dto.search.SearchPostDto(");
        jpql.append("   c.postId, "); // ID

        // 제목
        if (isShorts) {
            jpql.append("   c.title, ");
        } else {
            jpql.append("   null, ");
        }

        // 내용, 닉네임, 프로필, 날짜
        jpql.append("   p.content, ");
        jpql.append("   m.nickname, ");
        jpql.append("   m.profile_img, ");
        jpql.append("   p.writeDate, ");

        // 썸네일 (이미지 파일만, path 사용)
        jpql.append("   (SELECT f.path FROM File f WHERE f.post = p AND f.contentType LIKE 'image%' ORDER BY f.id ASC LIMIT 1), ");

        // 좋아요, 댓글 수
        jpql.append("   p.likeCount, ");
        jpql.append("   p.replyCount, ");

        // [추가] 그룹 ID (그룹 포스트일 때만 가져옴)
        if (isGroupPost) {
            jpql.append("   c.groupContents.id ");
        } else {
            jpql.append("   null ");
        }

        jpql.append(") ");

        // 2. FROM & JOIN
        jpql.append("FROM " + entityName + " c ");
        jpql.append("JOIN c.post p ");
        jpql.append("JOIN p.member m ");

        // 3. WHERE 절
        jpql.append("WHERE 1=1 ");

        // [보안 & 필터] 그룹 포스트 로직
        if (isGroupPost) {
            if (memberId == null) {
                // 비로그인 유저는 그룹 포스트 조회 불가
                jpql.append("AND 1=0 ");
            } else {
                // 1. 권한 체크 (가입한 그룹인지)
                jpql.append("AND EXISTS (SELECT 1 FROM GroupMember gm WHERE gm.groupContents = c.groupContents AND gm.member.id = :memberId) ");

                // 2. 특정 그룹 필터링 (사용자가 콤보박스에서 선택했을 때)
                if (groupId != null && groupId > 0) {
                    jpql.append("AND c.groupContents.id = :groupId ");
                }
            }
        }

        // 키워드 검색
        if (isShorts) {
            jpql.append("AND (c.title LIKE :keyword OR p.content LIKE :keyword) ");
        } else {
            jpql.append("AND (p.content LIKE :keyword) ");
        }

        // 기간 필터
        if (!"all".equals(period)) {
            jpql.append("AND p.writeDate >= :startDate ");
        }

        // 4. ORDER BY
        if ("likes".equals(sort)) {
            jpql.append("ORDER BY p.likeCount DESC, p.id DESC");
        } else if ("replies".equals(sort)) {
            jpql.append("ORDER BY p.replyCount DESC, p.id DESC");
        } else {
            jpql.append("ORDER BY p.writeDate DESC");
        }

        // 5. 쿼리 생성
        TypedQuery<SearchPostDto> query = em.createQuery(jpql.toString(), SearchPostDto.class);
        query.setParameter("keyword", "%" + keyword + "%");

        // [파라미터 바인딩 통합]
        if (isGroupPost && memberId != null) {
            query.setParameter("memberId", memberId);

            // 특정 그룹 선택 시 바인딩
            if (groupId != null && groupId > 0) {
                query.setParameter("groupId", groupId);
            }
        }

        if (!"all".equals(period)) {
            LocalDateTime startDate = LocalDateTime.now();
            if ("today".equals(period)) startDate = startDate.minusDays(1);
            else if ("month".equals(period)) startDate = startDate.minusMonths(1);
            else if ("year".equals(period)) startDate = startDate.minusYears(1);

            query.setParameter("startDate", java.sql.Timestamp.valueOf(startDate));
        }

        // 6. 페이징
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        return query.getResultList();
    }
}