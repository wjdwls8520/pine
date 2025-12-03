package com.site.pine.service;

import com.site.pine.dto.FileDto;
import com.site.pine.dto.community.PostReqDto;
import com.site.pine.dto.community.PostResDto;
import com.site.pine.entity.File;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository cr;
    private final S3UploadService sus;
    private final FileRepository fr;

    public void insertPost(PostReqDto reqDto) throws IOException {
        //post저장
        Post postEntity = new Post();
        postEntity.setCategory(reqDto.getCategory());
        postEntity.setContent(reqDto.getPostBody());
        postEntity.setStatus(reqDto.getStatus());
        cr.save(postEntity);

        // 파일테이블 저장 및 s3업로드
        List<MultipartFile> fileList = reqDto.getFiles();
        for(MultipartFile file : fileList) {
            String fileUrl = sus.saveFile(file); // S3 업로드
            File fileEntity = new File();
            // 포스트 조인
            fileEntity.setPost(postEntity);

            // s3저장
            fileEntity.setPath(fileUrl);

            // 기본파일객체 정보 저장
            fileEntity.setOriginalname(file.getOriginalFilename());
            fileEntity.setContentType(file.getContentType());
            fileEntity.setSize(file.getSize());
            fileEntity.setPageType("community");

            fr.save(fileEntity);
        }


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

            // 파일디티오
            List<FileDto> postFilesResult = new ArrayList<>(); // 파일을 담을 빈배열
            List<File> postFIies = postEntity.getFiles();  // 포스안에 들어있는 파일엔티티들
            for(File postFile : postFIies) {   // 포스엔티티안에 있는 파일엔티티개수만큼 반복
                FileDto filedto = new FileDto();  // 파일디티오 소환
                filedto.setId(postFile.getId());   // 파일디티오에 값넣기
                filedto.setPageType(postFile.getPageType());
                filedto.setOriginalname(postFile.getOriginalname());
                filedto.setSize(postFile.getSize());
                filedto.setPath(postFile.getPath());
                filedto.setContentType(postFile.getContentType());
                postFilesResult.add(filedto); // 파일을넣을 빈 배열에 파일 디티오 넣음
            };
            resDto.setFile(postFilesResult); // 포스트디티오에 files에 fildeDto를 담은 배열 넣음

            // "2번"의 빈 배열에 포스트dto 넣음
            list.add(resDto);
        }
        // "1번"의 빈 해시맵에 dto(포스트와 파일)가 모두 들어간 "2번"배열을 넣음
         result.put("postList",list);

        // "1번"의 빈 해시맵에 "4번"의 페이지객체에 담겨있는 토탈페이지를 넣음
         result.put("totalPage",postPages.getTotalPages());
         return result;
    }

    public int updateLikeCount(Long postId, boolean like) {
        Post post = cr.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        if(like) {
            post.setLikeCount(post.getLikeCount() + 1);
        } else {
            post.setLikeCount(post.getLikeCount() - 1);
        }

        cr.save(post);
        return post.getLikeCount();
    }


    public PostResDto getDetail(Long id) {
        Post post = cr.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다. id=" + id));

        // 엔티티 → DTO 변환
        PostResDto dto = new PostResDto();
        dto.setId(post.getId());
        dto.setContent(post.getContent());
        dto.setLikeCount(post.getLikeCount());
        dto.setReplyCount(post.getReplyCount());
        dto.setStatus(post.getStatus());
        dto.setCategory(post.getCategory());
        dto.setWriteDate(post.getWriteDate());
        dto.setUpdateDate(post.getUpdateDate());

        // 파일 DTO 리스트 만들 준비
        List<FileDto> postFilesResult = new ArrayList<>();

        // 게시글 엔티티 안에 있는 파일 리스트 꺼내오기
        List<File> postFiles = post.getFiles();

        // 파일 개수만큼 반복
        for (File postFile : postFiles) {
            // FileDto 객체 생성
            FileDto fileDto = new FileDto();
            // File 엔티티 → FileDto 로 값 복사
            fileDto.setId(postFile.getId());
            fileDto.setPageType(postFile.getPageType());
            fileDto.setOriginalname(postFile.getOriginalname());
            fileDto.setSize(postFile.getSize());
            fileDto.setPath(postFile.getPath());
            fileDto.setContentType(postFile.getContentType());
            // 리스트에 추가
            postFilesResult.add(fileDto);
        }
        // DTO에 파일 리스트 넣기
        dto.setFile(postFilesResult);

        return dto;
    }
}
