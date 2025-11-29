package com.site.pine.service;

import com.site.pine.dto.community.PostReqDto;
import com.site.pine.dto.community.PostResDto;
import com.site.pine.entity.post.Post;
import com.site.pine.repository.CommunityRepository;
import com.site.pine.repository.FileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository cr;
    private final S3UploadService ss;
    private final FileRepository fr;

    public void insertPost(PostReqDto reqDto)  {
        Post postEntity = new Post();
        postEntity.setCategory(reqDto.getCategory());
        postEntity.setContent(reqDto.getPostBody());
        postEntity.setStatus(reqDto.getStatus());
        cr.save(postEntity);

    }

    public HashMap<String, Object> getPostPage(Integer page) {
        //1. 빈 해시맵 만들기
        HashMap<String, Object> result = new HashMap<>();
        // 2. 빈 리스트 만들기
        List<PostResDto> list = new ArrayList<>();

        // 3. 페이지 정의 = page번째 페이지에서 6 개씩 가져와라 라는 정보 담음
        Pageable pageable = PageRequest.of(page, 6);
        // 4. 페이지객체에 포스트엔티티 넣기 (페이지네이션 된 데이터만 가져옴)
        // Page<Post> 에는 페이지정보가 포함되어있음.
        Page<Post> postPages = cr.findAllByOrderByWriteDateDesc(pageable);

        // 5. 포스트앤티티들을 반복문을 적용해 각각의 dto에 값을 넣음
        for(Post postEntity : postPages) {
            PostResDto resDto = new PostResDto();
            resDto.setId(postEntity.getId());
            resDto.setCategory(postEntity.getCategory());
            resDto.setContent(postEntity.getContent());
            resDto.setLikeCount(postEntity.getLikeCount());
            resDto.setReplyCount(postEntity.getReplyCount());
            resDto.setStatus(postEntity.getStatus());
            resDto.setWriteDate(postEntity.getWriteDate());
            resDto.setUpdateDate(postEntity.getUpdateDate());
            // "2번"의 빈 배열에 dto 넣음
            list.add(resDto);
        }
        // "1번"의 빈 해시맵에 dto가 들어간 "2번"배열을 넣음
         result.put("postList",list);
        // "1번"의 빈 해시맵에 "4번"의 페이지객체에 담겨있는 토탈페이지를 넣음
         result.put("totalPage",postPages.getTotalPages());
         return result;
    }
}
