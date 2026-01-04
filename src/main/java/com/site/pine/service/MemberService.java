package com.site.pine.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.site.pine.dao.IMemberDao;
import com.site.pine.dto.community.PostListDto;
import com.site.pine.dto.member.CountryDto;
import com.site.pine.dto.member.MemberDto;
import com.site.pine.dto.member.MemberJoinDto;
import com.site.pine.entity.Member;
import com.site.pine.repository.PostRepository;
import com.site.pine.repository.MemberRepository;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.sql.Timestamp;
import java.util.*;

@Service
public class MemberService {

    @Autowired
    IMemberDao mdao;

    @Autowired
    MemberRepository mr;

    @Autowired
    PostRepository cr;

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

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("5a177020f5fbeb60757eb1697251438649db35eb6d7383bd9db462ad216af0e2")
    private String serviceKey;

    public List<CountryDto> getCountryList() {

        String url =
                "https://api.odcloud.kr/api/15091117/v1/uddi:bbcc2939-88e0-4a54-af03-ab819b4130e6"
                        + "?page=1"
                        + "&perPage=300"
                        + "&serviceKey=" + serviceKey;

        Map response = restTemplate.getForObject(url, Map.class);

        List<Map<String, Object>> data =
                (List<Map<String, Object>>) response.get("data");

        List<CountryDto> result = new ArrayList<>();

        for (Map<String, Object> row : data) {
            String kor = Objects.toString(row.get("한글명"), "");
            String eng = Objects.toString(row.get("영문명"), "");
            String iso = Objects.toString(row.get("ISO alpha2"), "");

            result.add(new CountryDto(kor, eng, iso));

//            System.out.println(row);
        }

        if(result.isEmpty()) {
            System.out.println("뭔가 잘못됨");
        }else{
            System.out.println("result:"+result.size());
        }

        return result;
    }
}
