package com.site.pine.service;

import com.site.pine.dao.IMemberDao;
import com.site.pine.dto.community.PostListDto;
import com.site.pine.dto.community.PostResDto;
import com.site.pine.dto.member.MemberDto;
import com.site.pine.dto.member.MemberJoinDto;
import com.site.pine.entity.Member;
import com.site.pine.repository.CommunityRepository;
import com.site.pine.repository.MemberRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
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
        member.setResident_num("0");
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
    public List<PostListDto> getPostList(int id) {
        return cr.findPostList();
    }
}
