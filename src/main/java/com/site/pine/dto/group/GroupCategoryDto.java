package com.site.pine.dto.group;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupCategoryDto {
    private Long id;

    private Integer categoryId;
    private String nameKor;
    private String nameEng;
}
