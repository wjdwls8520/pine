package com.site.pine.security;

import com.site.pine.dto.member.MemberDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final OAuth2User oauth2User;
    private final MemberDto member; // DB 사용자 정보

    public CustomOAuth2User(OAuth2User oauth2User, MemberDto member) {
        this.oauth2User = oauth2User;
        this.member = member;
    }

    // 🔹 우리 도메인 정보
    public String getEmail() {
        return member != null ? member.getEmail()
                : oauth2User.getAttribute("email");
    }

    public String getNickname() {
        return member != null ? member.getNickname() : null;
    }

    public Long getId() {
        return member != null ? member.getId() : null;
    }

    public String getPhone() {
        return member != null ? member.getPhone() : null;
    }

    public Integer getProvider() {
        return member != null ? member.getProvider() : null;
    }

    public String getProfileimg() {
        return member != null ? member.getProfileimg() : null;
    }

    // 🔹 OAuth2User 필수 구현
    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oauth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return oauth2User.getName();
    }

}
