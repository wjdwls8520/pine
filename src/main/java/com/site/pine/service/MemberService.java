package com.site.pine.service;

import com.site.pine.dao.IMemberDao;
import com.site.pine.dto.member.MemberDto;
import com.site.pine.dto.member.MemberJoinDto;
import com.site.pine.entity.Member;
import com.site.pine.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;

@Service
public class MemberService {

    @Autowired
    IMemberDao mdao;

    @Autowired
    MemberRepository mr;

    public MemberJoinDto getMemberInfo(String email, String nickname) {
        MemberJoinDto mdto = new MemberJoinDto();
        Member memberByEmail = mr.findByEmail(email);
        Member memberByNickname = mr.findByNickname(nickname);
        if (memberByNickname != null) {
            mdto.setEmail(memberByEmail.getEmail());
        }else{
            mdto.setEmail("");
        }

        if (memberByNickname != null) {
            mdto.setNickname(memberByNickname.getNickname());
        }else {
            mdto.setNickname("");
        }

        return mdto;
    }

    public void insertMember(String email, String name, String nickname, String job, String addressCode, String address1, String address2, String profile_msg, String phone, MemberDto mdto) {

        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());

        Member member = new Member();
        member.setEmail(email);
        member.setName(name);
        member.setNickname(nickname);
        member.setJob(job);
        member.setAddress_code(addressCode);
        member.setAddress_1(address1);
        member.setAddress_2(address2);
        member.setJoin_date(timestamp);
        member.setPhone(mdto.getPhone());
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
        member.setResident_num("0");
        mr.save(member);

    }

    public MemberDto getMember(String email) {
        MemberDto mdto = new MemberDto();
        Member member = mr.findByEmail(email);
        mdto.setEmail(member.getEmail());
        mdto.setNickname(member.getNickname());
        mdto.setName(member.getName());
        mdto.setProvider(member.getProvider());
        return mdto;
    }


    public MemberDto findByEmail(String email) {

        MemberDto member = new MemberDto();
        Member memberByEmail = mr.findByEmail(email);
        member.setEmail(memberByEmail.getEmail());
        return member;
    }
}
