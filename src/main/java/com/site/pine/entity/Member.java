package com.site.pine.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer m_idx;

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
    private String provider;
    @Column(nullable = false)
    private String position;
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

}
