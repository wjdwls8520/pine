package com.site.pine.dto.tag;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TagResDto {
    private Long id;
    private String name;
    private Long targetId;
}
