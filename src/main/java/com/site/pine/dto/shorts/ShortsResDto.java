package com.site.pine.dto.shorts;

import com.site.pine.dto.FileDto;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class ShortsResDto {
    private Long id;

    private String title;

    private String content;

    private Timestamp indate;

    private Timestamp updateDate;

    private List<FileDto> files;
}
