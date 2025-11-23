package com.site.pine.entity.group;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GroupCategory {
    @Id
    private Integer id;

    private String nameKor;

    private String nameEng;

}
