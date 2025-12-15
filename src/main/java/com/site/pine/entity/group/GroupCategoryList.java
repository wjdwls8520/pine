package com.site.pine.entity.group;

import jakarta.persistence.*;
import lombok.*;

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

}
