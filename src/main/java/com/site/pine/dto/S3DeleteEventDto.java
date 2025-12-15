package com.site.pine.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class S3DeleteEventDto {
    private final String path;
}
