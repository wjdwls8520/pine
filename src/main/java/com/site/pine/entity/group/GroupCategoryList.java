package com.site.pine.entity.group;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupCategoryList {
    @Id
    private Integer id;

    private String nameKor;

    private String nameEng;

    @Comment("그룹에 해당하는 카테고리")
    @OneToMany(
            mappedBy = "categoryId",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<GroupInCategory> categoryIds = new ArrayList<>();

    public GroupCategoryList(Integer id, String nameKor, String nameEng) {
        this.id = id;
        this.nameKor = nameKor;
        this.nameEng = nameEng;
    }

}
