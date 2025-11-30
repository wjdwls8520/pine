package com.site.pine.dto.shorts;

import com.site.pine.dto.FileDto;
import com.site.pine.entity.File;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Data;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
public class ShortsResDto {
    private Long id;

    private String title;

    private String content;

    private Timestamp indate;

    private Timestamp updateDate;

    private List<FileDto> files;
}
