package com.site.pine.dto;

import com.site.pine.entity.S3FileDeleteFailList;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class S3DeleteEventDto {
    private final String pageType;
    private final String originalFilename;
    private final Long size;
    private final String path;
}
