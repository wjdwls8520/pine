package com.site.pine.repository.search;

import com.site.pine.dto.search.SearchPostDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostSearchRepository {
    // [수정] 메서드 시그니처에 Long groupId 추가
    <T> List<SearchPostDto> searchPosts(Class<T> entityType, String keyword, Pageable pageable, String sort, String period, Long memberId, Long groupId);
}