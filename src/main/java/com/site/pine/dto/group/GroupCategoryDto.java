package com.site.pine.dto.group;

import lombok.Data;

@Data
public class GroupCategoryDto {
    private Long id;

    private Integer categoryId;
    private String nameKor;
    private String nameEng;
}
