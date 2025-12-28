package com.site.pine.dto.shorts;

import com.site.pine.enums.PageType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShortsFileDto {

    private Long fileId;
    private Long shortsId;
    private PageType pageType;
    private String path;
    private String contentType;

    public ShortsFileDto(
            Long fileId,
            Long shortsId,
            PageType pageType,
            String path,
            String contentType
    ) {
        this.fileId = fileId;
        this.shortsId = shortsId;
        this.pageType = pageType;
        this.path = path;
        this.contentType = contentType;
    }
}
