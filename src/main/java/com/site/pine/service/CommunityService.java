package com.site.pine.service;

import com.site.pine.dto.community.PostReqDto;
import com.site.pine.dto.community.PostResDto;
import com.site.pine.entity.post.Post;
import com.site.pine.repository.CommunityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository cr;

    public void insertPost(PostReqDto reqDto) {
        Post postEntity = new Post();
        postEntity.setCategory(reqDto.getCategory());
        postEntity.setContent(reqDto.getPostBody());
        postEntity.setStatus(reqDto.getStatus());
        cr.save(postEntity);
    }

    public List<PostResDto> getAllPost() {
        List<PostResDto> resDtoList = new ArrayList<>();

        List<Post> postsEntity = cr.findAllByOrderByWriteDateDesc();
        for(Post postEntity : postsEntity ) {
            PostResDto resDto = new PostResDto();
            resDto.setId(postEntity.getId());
            resDto.setCategory(postEntity.getCategory());
            resDto.setContent(postEntity.getContent());
            resDto.setLikeCount(postEntity.getLikeCount());
            resDto.setReplyCount(postEntity.getReplyCount());
            resDto.setStatus(postEntity.getStatus());
            resDto.setWriteDate(postEntity.getWriteDate());
            resDto.setUpdateDate(postEntity.getUpdateDate());

            resDtoList.add(resDto);
        }

        return resDtoList;
    }
}
