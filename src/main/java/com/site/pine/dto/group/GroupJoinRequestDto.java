package com.site.pine.dto.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class GroupJoinRequestDto {

    @NotNull
    private Long groupId;

    @NotBlank
    @Size(max = 200)
    private String introduction;

}
