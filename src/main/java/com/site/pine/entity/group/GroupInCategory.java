package com.site.pine.entity.group;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupInCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer categoryId;
    private String categoryNameKor;
    private String categoryNameEng;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="groupContents")
    private GroupContents groupContents;
}
