package com.site.pine.entity.group;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class GroupInCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer categoryId;
    private String categoryName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="groupContents")
    private GroupContents groupContents;
}
