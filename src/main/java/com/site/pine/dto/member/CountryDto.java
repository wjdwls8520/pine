package com.site.pine.dto.member;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@AllArgsConstructor
public class CountryDto {
    private String countryNm;      // 한글명
    private String countryEngNm;   // 영문명
    private String isoAlpha2;      // ISO 2자리
}