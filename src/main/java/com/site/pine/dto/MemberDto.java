package com.site.pine.dto;

import lombok.Data;

@Data
public class MemberDto {
    private int id;
    private String email;
    private String pwd;
    private String nickname;
    private String phone;
    private String provider;
    private String snsid;
    private String profileimg;
    private String profilemsg;
}
