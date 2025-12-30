package com.site.pine.mapper;

import com.site.pine.dto.S3DeleteEventDto;
import com.site.pine.entity.File;
import com.site.pine.entity.S3FileDeleteFailList;
import org.springframework.stereotype.Component;

@Component
public class S3FileDeleteFailMapper {

    public S3FileDeleteFailList toS3FileDeleteFailMapper(S3DeleteEventDto event, Exception e) {
        S3FileDeleteFailList s3FileDeleteFailList = new S3FileDeleteFailList();
        s3FileDeleteFailList.setOriginalname(event.getOriginalFilename());
        s3FileDeleteFailList.setSize(event.getSize());
        s3FileDeleteFailList.setPath(event.getPath());
        s3FileDeleteFailList.setErrorMessage(e.getMessage());
        return s3FileDeleteFailList;
    }

    public S3FileDeleteFailList toS3FileDeleteFailMapper(File oldFile, Exception e) {
        S3FileDeleteFailList s3FileDeleteFailList = new S3FileDeleteFailList();
        s3FileDeleteFailList.setOriginalname(oldFile.getOriginalname());
        s3FileDeleteFailList.setSize(oldFile.getSize());
        s3FileDeleteFailList.setPath(oldFile.getPath());
        s3FileDeleteFailList.setErrorMessage(e.getMessage());
        return s3FileDeleteFailList;
    }


}
