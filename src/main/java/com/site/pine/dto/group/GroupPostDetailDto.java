package com.site.pine.dto.group;

import com.site.pine.entity.File;
import com.site.pine.entity.group.GroupPost;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GroupPostDetailDto {
    private Long id;
    private String content;
    private Integer status;

    private List<String> tags;
    private List<File> files;

    public GroupPostDetailDto(GroupPost cp, List<String> tags) {
        this.id = cp.getPost().getId();
        this.content = cp.getPost().getContent();
        this.status = cp.getPost().getStatus();

        this.tags = tags;
        this.files = cp.getPost().getFiles();
    }
}



