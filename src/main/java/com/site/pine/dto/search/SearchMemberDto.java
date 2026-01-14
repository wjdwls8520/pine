package com.site.pine.dto.search;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchMemberDto {
    private Long id;
    private String nickname;
    private String profileImg;
    private String profileMsg;
}