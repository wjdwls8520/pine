package com.site.pine.dto.group;

import lombok.Getter;

@Getter
public class SimpleFileJpqlResDto {
    Long id;
    String path;

    public SimpleFileJpqlResDto(Long id, String path) {
        this.id = id;
        this.path = path;
    }
}
