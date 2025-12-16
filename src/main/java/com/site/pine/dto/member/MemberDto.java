package com.site.pine.dto.member;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
public class MemberDto {
    private Long id;
    private String email;
    private String pwd;
    private String name;
    private String nickname;
    private String phone;
    private int provider;
    private String snsid;
    private String profileimg;
    private String profilemsg;

    private Boolean terms_agreed;
    private Timestamp terms_agreed_date;

    private Boolean privacy_agreed;
    private Timestamp privacy_agreed_date;

    private Boolean marketing_agreed;
    private Timestamp marketing_agreed_date;

    private int position;
}
