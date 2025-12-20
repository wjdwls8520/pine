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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="category_id")
    private GroupCategoryList categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="group_contents_id")
    private GroupContents groupContents;

}
