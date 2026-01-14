package com.site.pine.repository.group;

import com.site.pine.dto.search.SearchGroupDto;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface GroupContentsRepositoryCustom {
    // 검색어, 페이징, 정렬조건(sort)을 받는 메서드 선언
    List<SearchGroupDto> searchGroups(String keyword, Pageable pageable, String sort);
}