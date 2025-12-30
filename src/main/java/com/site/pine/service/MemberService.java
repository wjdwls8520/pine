package com.site.pine.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.site.pine.config.WebClientConfig;
import com.site.pine.dao.IMemberDao;
import com.site.pine.dto.community.PostListDto;
import com.site.pine.dto.member.CountryDto;
import com.site.pine.dto.member.MemberDto;
import com.site.pine.dto.member.MemberJoinDto;
import com.site.pine.entity.Member;
import com.site.pine.repository.CommunityRepository;
import com.site.pine.repository.MemberRepository;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MemberService {

    @Autowired
    IMemberDao mdao;

    @Autowired
    MemberRepository mr;

    @Autowired
    CommunityRepository cr;

    public MemberJoinDto getMemberInfo(String email, String nickname) {
        MemberJoinDto mdto = new MemberJoinDto();
        Member memberByEmail = mr.findByEmail(email);
        Member memberByNickname = mr.findByNickname(nickname);
        if (memberByEmail == null) {
            mdto.setEmail("");
        }else{
            mdto.setEmail(memberByEmail.getEmail());
        }

        if (memberByNickname == null) {
            mdto.setNickname("");
        }else {
            mdto.setNickname(memberByNickname.getNickname());
        }

        return mdto;
    }

    public void insertMember(String email, String name, String nickname, String job, String country, String addressCode, String address1, String address2, String profile_msg, String phone, MemberDto mdto) {

        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());

        Member member = new Member();
        member.setEmail(email);
        member.setName(name);
        member.setNickname(nickname);
        member.setJob(job);
        member.setCountry(country);
        member.setAddress_code(addressCode);
        member.setAddress_1(address1);
        member.setAddress_2(address2);
        member.setJoin_date(timestamp);
        if(mdto.getPhone()!=null){
            member.setPhone(mdto.getPhone());
        }else {
            member.setPhone(phone);
        }
        member.setPrivacy_agreed(mdto.getPrivacy_agreed());
        member.setPrivacy_agreed_date(mdto.getPrivacy_agreed_date());
        member.setProvider(mdto.getProvider());
        member.setTerms_agreed(mdto.getTerms_agreed());
        member.setTerms_agreed_date(mdto.getTerms_agreed_date());
        member.setMarketing_agreed(mdto.getMarketing_agreed());
        member.setMarketing_agreed_date(mdto.getMarketing_agreed_date());
        member.setProfile_img(mdto.getProfileimg());
        member.setProfile_msg(profile_msg);
        member.setAgreed_version("1.0");
        member.setLevel(0);
        member.setPosition(mdto.getPosition());
        mr.save(member);

    }

    public MemberDto getMember(String email) {
        MemberDto mdto = new MemberDto();
        Member member = mr.findByEmail(email);

        System.out.println("member : "+member);

        mdto.setId(member.getId());
        mdto.setEmail(member.getEmail());
        mdto.setNickname(member.getNickname());
        mdto.setName(member.getName());
        mdto.setProvider(member.getProvider());
        mdto.setPhone(member.getPhone());
        mdto.setProfileimg(member.getProfile_img());
        return mdto;
    }


    public MemberDto findByEmail(String email) {

        MemberDto member = new MemberDto();
        Member memberByEmail = mr.findByEmail(email);
        if(memberByEmail == null){
            member = null;
        }else{
            member.setEmail(memberByEmail.getEmail());
        }
        return member;
    }

    public boolean existsByEmail(String email) {
        return mr.findByEmail(email) != null;
    }

    @Transactional(readOnly = true)
    public List<PostListDto> getPostList(Long id) {
        return cr.findPostList();
    }

    @Autowired
    WebClient webClient;
    private List<CountryDto> cachedCountries = new ArrayList<>();

    public MemberService(WebClient webClient) {
        this.webClient = webClient;
    }

    @PostConstruct
    public void loadCountries() {

        String uri =
                "/getCountryCodeList3"
                        + "?serviceKey=" + "5a177020f5fbeb60757eb1697251438649db35eb6d7383bd9db462ad216af0e2"
                        + "&numOfRows=300"
                        + "&pageNo=1"
                        + "&type=json";

        JsonNode root = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        JsonNode items = root.path("response")
                .path("body")
                .path("items")
                .path("item");

        for (JsonNode node : items) {
            CountryDto dto = new CountryDto();
            dto.setCountry_nm(node.path("country_nm").asText());
            dto.setCountry_eng_nm(node.path("country_eng_nm").asText());
            dto.setCountry_iso_alp2(node.path("country_iso_alp2").asText());
            cachedCountries.add(dto);
        }
    }

    public List<CountryDto> searchCountry(String keyword) {
        String lower = keyword.toLowerCase();

        return cachedCountries.stream()
                .filter(c ->
                        c.getCountry_nm().contains(keyword) ||
                                c.getCountry_eng_nm().toLowerCase().contains(lower)
                )
                .limit(10)
                .toList();
    }
}
