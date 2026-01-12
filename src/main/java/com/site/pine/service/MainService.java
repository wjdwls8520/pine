package com.site.pine.service;

import com.site.pine.dto.group.GroupContentsJpqlResDto;
import com.site.pine.dto.post.PostAllDto;
import com.site.pine.repository.PostRepository;
import com.site.pine.repository.group.GroupContentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainService {

    private final PostRepository postRepository;
    private final GroupContentsRepository groupContentsRepository;

    @Transactional(readOnly = true)
    public List<PostAllDto> getBestPost() {
        Pageable limitSix = PageRequest.of(0, 6);
        return postRepository.findAllBestPost(limitSix);
    }

    @Transactional(readOnly = true)
    public List<GroupContentsJpqlResDto> getBestGroup() {
        Pageable limitSix = PageRequest.of(0, 12);
        return groupContentsRepository.findGroupBestResDto(limitSix);
    }
}
