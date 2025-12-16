package com.site.pine.entity;

import com.site.pine.entity.group.GroupMember;
import com.site.pine.entity.post.Post;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String nickname;
    @Column(nullable = false)
    private String resident_num;
    @Column(nullable = false)
    private String phone;
    @Column(nullable = false)
    private Integer level;
    @Column(nullable = false)
    private String address_code;
    @Column(nullable = false)
    private String address_1;
    @Column(nullable = false)
    private String address_2;
    private String job;
    private String profile_img;
    private String profile_msg;
    @Column(nullable = false)
    private int provider;
    @Column(nullable = false)
    private int position;
    @Column(nullable = false)
    private Timestamp join_date;
    @Column(nullable = false)
    private Boolean terms_agreed;
    @Column(nullable = false)
    private Timestamp terms_agreed_date;
    @Column(nullable = false)
    private Boolean privacy_agreed;
    @Column(nullable = false)
    private Timestamp privacy_agreed_date;
    @Column(nullable = false)
    private Boolean marketing_agreed;
    @Column(nullable = false)
    private Timestamp marketing_agreed_date;
    @Column(nullable = false, columnDefinition = "varchar(45) default '1.0'")
    private String agreed_version;


    @Comment("커뮤니티 포스트를 작성한 멤버")
    @OneToMany(
            mappedBy = "member",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Post> communityPost = new ArrayList<>();


    @Comment("그룹에 가입된 멤버")
    @OneToMany(
            mappedBy = "member",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<GroupMember> groupMembers = new ArrayList<>();


}
