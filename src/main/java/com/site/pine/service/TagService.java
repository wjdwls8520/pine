package com.site.pine.service;

import com.site.pine.dto.tag.TagResDto;
import com.site.pine.entity.Tag;
import com.site.pine.entity.TagMapping;
import com.site.pine.repository.TagMappingRepository;
import com.site.pine.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TagService {

    private final TagRepository tr;
    private final TagMappingRepository tmr;

    /**
     * 태그 등록/수정 (기존 태그 삭제 후 재등록)
     * @param postId 게시글(Post)의 ID
     * @param tagString 화면에서 넘어온 태그 문자열 (예: "여행, 맛집, 서울")
     */
    public void updateTags(Long postId, String tagString) {
        // 1. 기존 태그 매핑 삭제 (초기화)
        tmr.deleteByTargetId(postId);
        tmr.flush(); // 즉시 DB 반영 (delete 후 insert 충돌 방지)

        // 태그가 비어있으면 종료
        if (tagString == null || tagString.isBlank()) return;

        // 2. 문자열 파싱 ("여행, 맛집" -> ["여행", "맛집"])
        List<String> tagNames = Arrays.stream(tagString.split(","))
                .map(String::trim)        // 공백 제거
                .filter(s -> !s.isEmpty()) // 빈 문자열 제거
                .distinct()                // 중복 제거
                .toList();

        // 3. 저장
        for (String tagName : tagNames) {
            // 태그 단어 저장 (있으면 가져오고, 없으면 생성)
            Tag tag = tr.findByName(tagName)
                    .orElseGet(() -> tr.save(new Tag(null, tagName)));

            // 매핑 저장
            TagMapping mapping = new TagMapping();
            mapping.setTag(tag);
            mapping.setTargetId(postId); // 여기에 Post ID 저장

            tmr.save(mapping);
        }
    }

    /**
     * 태그 조회 (문자열 리스트 반환)
     */
    @Transactional(readOnly = true)
    public List<String> getTags(Long postId) {
        return tmr.findAllByTargetId(postId)
                .stream()
                .map(mapping -> mapping.getTag().getName())
                .collect(Collectors.toList());
    }

    /**
     * 태그 삭제 (게시글 삭제 시 호출)
     */
    public void deleteTags(Long postId) {
        tmr.deleteByTargetId(postId);
    }

    // 여러 게시글의 태그를 한방에 조회 (DTO 반환)
    @Transactional(readOnly = true)
    public List<TagResDto> getTagsByPostIds(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return List.of();
        }
        return tmr.findTagsByTargetIds(postIds);
    }
}