package com.site.pine.dto.community;

import com.site.pine.entity.File;
import com.site.pine.entity.community.CommunityPost;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PostDetailDto {
    private Long id;
    private String content;
    private Integer category;
    private Integer status;

    private List<String> tags;
    private List<File> files;

    public PostDetailDto(CommunityPost cp, List<String> tags) {
        this.id = cp.getPost().getId();
        this.content = cp.getPost().getContent();
        this.category = cp.getCategory();
        this.status = cp.getPost().getStatus();

        this.tags = tags;
        this.files = cp.getPost().getFiles();
    }
}
