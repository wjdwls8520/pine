package com.site.pine.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class FileDto {
    private Long id;
    private String originalname;
    private Long size;
    private String path;
    private String contentType;
    private Timestamp indate;
}
