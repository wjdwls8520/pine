package com.site.pine.repository.group;

import com.site.pine.dto.search.SearchGroupDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

// no usages 떠도 삭제하면 안됨!!!
// 이 코드는 스프링이 실행될 때(Runtime) 몰래 가져가서 사용함.
@RequiredArgsConstructor
public class GroupContentsRepositoryImpl implements GroupContentsRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<SearchGroupDto> searchGroups(String keyword, Pageable pageable, String sort) {
        StringBuilder jpql = new StringBuilder();

        // 1. SELECT (순서: id, name, desc, img, member, like)
        jpql.append("SELECT new com.site.pine.dto.search.SearchGroupDto(");
        jpql.append("   gc.id, gc.groupName, gc.groupDescription, f.path, gc.groupMemberCount, gc.likeCount ");
        jpql.append(") ");

        jpql.append("FROM GroupContents gc ");
        jpql.append("LEFT JOIN gc.file f ");

        jpql.append("WHERE (gc.groupName LIKE :keyword OR gc.groupDescription LIKE :keyword) ");

        if ("members".equals(sort)) {
            jpql.append("ORDER BY gc.groupMemberCount DESC, gc.id DESC");
        } else if ("likes".equals(sort)) {
            jpql.append("ORDER BY gc.likeCount DESC, gc.id DESC");
        } else {
            jpql.append("ORDER BY gc.indate DESC");
        }

        TypedQuery<SearchGroupDto> query = em.createQuery(jpql.toString(), SearchGroupDto.class);
        query.setParameter("keyword", "%" + keyword + "%");
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        return query.getResultList();
    }
}