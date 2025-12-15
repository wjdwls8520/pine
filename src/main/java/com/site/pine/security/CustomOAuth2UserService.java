package com.site.pine.security;

import com.site.pine.dto.member.MemberDto;
import com.site.pine.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService
        implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberService memberService;

    private final DefaultOAuth2UserService delegate =
            new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request)
            throws OAuth2AuthenticationException {

        OAuth2User oauthUser = delegate.loadUser(request);

        String email = oauthUser.getAttribute("email");

        boolean exists = memberService.existsByEmail(email);

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        MemberDto member = null;

        if (exists) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            member = memberService.getMember(email);
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_OAUTH"));
        }

        OAuth2User baseUser = new DefaultOAuth2User(
                authorities,
                oauthUser.getAttributes(),
                "email"
        );

        return new CustomOAuth2User(baseUser, member);
    }
}
