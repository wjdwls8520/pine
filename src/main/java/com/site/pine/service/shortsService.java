package com.site.pine.service;

import com.site.pine.dto.FileDto;
import com.site.pine.dto.community.PostResDto;
import com.site.pine.dto.shorts.ShortsResDto;
import com.site.pine.dto.shorts.ShortsUploadReqDto;
import com.site.pine.entity.File;
import com.site.pine.entity.post.Post;
import com.site.pine.entity.shorts.Shorts;
import com.site.pine.repository.FileRepository;
import com.site.pine.repository.ShortsRepository;
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
public class shortsService {

    private final ShortsRepository sr;
    private final FileRepository fr;
    private final S3UploadService sus;

    public void insertShorts(ShortsUploadReqDto shortsuploadreqdto) throws IOException {
        Shorts shortsEntity = new Shorts();
        shortsEntity.setTitle(shortsuploadreqdto.getTitle());
        shortsEntity.setContent(shortsuploadreqdto.getContent());
        sr.save(shortsEntity);

        for (MultipartFile file : shortsuploadreqdto.getFiles()) {

            String fileUrl = sus.saveFile(file); // S3 업로드

            File fileEntity = new File();
            fileEntity.setPageType("shorts");
            fileEntity.setOriginalname(file.getOriginalFilename());
            fileEntity.setSize(file.getSize());
            fileEntity.setPath(fileUrl);
            fileEntity.setContentType(file.getContentType());
            fileEntity.setShorts(shortsEntity);

            fr.save(fileEntity);
        }

    }

    public HashMap<String, Object> getAllShorts(int page) {
        //1. 빈 해시맵 만들기
        HashMap<String, Object> result = new HashMap<>();
        // 2. 빈 리스트 만들기
        List<ShortsResDto> list = new ArrayList<>();

        // 3. 페이지 정의 = page번째 페이지에서 6 개씩 가져와라 라는 정보 담음
        Pageable pageable = PageRequest.of(page, 2);
        // 4. 페이지객체에 쇼츠엔티티 넣기 (페이지네이션 된 데이터만 가져옴)
        // Page<Shorts> 에는 페이지정보가 포함되어있음.
//        Page<Shorts> shortsPages = sr.findAllByOrderByIndateDesc(pageable);
        Page<Shorts> shortsPages = sr.findAllByOrderByIndateDescIdDesc(pageable);
        for(Shorts shortsEntity : shortsPages) {
            ShortsResDto resDto = new ShortsResDto();
            resDto.setId(shortsEntity.getId());
            resDto.setTitle(shortsEntity.getTitle());
            resDto.setContent(shortsEntity.getContent());
            resDto.setIndate(shortsEntity.getIndate());
            resDto.setUpdateDate(shortsEntity.getUpdateDate());

            List<FileDto> fileDtoList = new ArrayList<>();
            for( File file : shortsEntity.getFiles()) {
                FileDto fileDto = new FileDto();
                fileDto.setId(file.getId());
                fileDto.setPageType(file.getPageType());
                fileDto.setOriginalname(file.getOriginalname());
                fileDto.setPath(file.getPath());
                fileDto.setContentType(file.getContentType());
                fileDto.setSize(file.getSize());
                fileDtoList.add(fileDto);
            }

            resDto.setFiles(fileDtoList);
            list.add(resDto);
        }
        result.put("shortsList", list);
        result.put("totalPage", shortsPages.getTotalPages());

        return result;
    }

}
