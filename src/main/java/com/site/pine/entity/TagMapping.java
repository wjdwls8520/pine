package com.site.pine.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table( //중복 태그 매핑 방지
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"tag_id", "targetId"}
                )
        }
)
public class TagMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("태그매핑 고유번호")
    private Long id;

    @Comment("대상 ID")
    @Column(nullable = false)
    private Long targetId;


    //tag테이블 조인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;
}
