package com.site.pine.dto;

import com.site.pine.enums.PageType;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class FileDto {
    private Long id;
    private PageType pageType;
    private String originalname;
    private Long size;
    private String path;
    private String contentType;
    private Timestamp indate;
}
