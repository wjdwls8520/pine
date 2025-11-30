package com.site.pine.controller;

import com.site.pine.dto.member.MemberDto;
import com.site.pine.dto.member.MemberJoinDto;
import com.site.pine.entity.Member;
import com.site.pine.repository.MemberRepository;
import com.site.pine.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.*;

import org.json.JSONObject;


@Controller
public class MemberController {

    @Autowired
    MemberService ms;

    @Autowired
    MemberRepository mr;


    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    @Value("${naver.redirect-uri}")
    private String redirectUriConfig;

    @GetMapping("/auth/naver/login")
    public void naverLogin(HttpServletResponse response) throws IOException {
        String state = UUID.randomUUID().toString();
        String redirectUri = URLEncoder.encode(redirectUriConfig, StandardCharsets.UTF_8);
        String naverUrl = "https://nid.naver.com/oauth2.0/authorize"
                + "?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&state=" + state;

        System.out.println("redirect_uri: " + redirectUri);
        System.out.println("naverUrl: " + naverUrl);
        response.sendRedirect(naverUrl); // 브라우저를 네이버 로그인 페이지로 이동
    }

    @GetMapping("/naver/callback")
    public String callback(@RequestParam String code,
                           @RequestParam String state,
                           HttpServletRequest request)  {

        System.out.println("code: " + code);
        System.out.println("state: " + state);
        System.out.println("request: " + request);

        // 1) 토큰 요청
        String tokenUrl = "https://nid.naver.com/oauth2.0/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("charset", "UTF-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("state", state);

        HttpEntity<MultiValueMap<String, String>> requestEntity =
                new HttpEntity<>(params, headers);

        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> tokenRes =
                rt.exchange(tokenUrl, HttpMethod.POST, requestEntity, String.class);

        System.out.println("tokenRes: " + tokenRes.getBody());

        // 2) access_token으로 사용자 정보 가져오기
        String accessToken = new JSONObject(tokenRes.getBody()).getString("access_token");
        HttpHeaders headers1 = new HttpHeaders();
        headers1.add("Authorization", "Bearer " + accessToken);
        ResponseEntity<String> userRes = rt.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                new HttpEntity<>(headers1),
                String.class
        );

        // 3) 사용자 정보 DB 처리
        JSONObject profile = new JSONObject(userRes.getBody()).getJSONObject("response");

        System.out.println(profile);
        String email = profile.getString("email");

        Member member = mr.findByEmail(email);

        if(member == null){
            MemberDto mdto = new MemberDto();
            mdto.setEmail(profile.optString("email"));
            mdto.setName(profile.optString("name"));
            mdto.setProfileimg(profile.optString("profile_image"));
            mdto.setPhone(profile.optString("mobile"));
            mdto.setProvider("NAVER");
            request.getSession().setAttribute("naveruserinfo", mdto);
            return "member/jointerms";
        }else{

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            member,                 // principal
                            null,                   // credentials
                            List.of(new SimpleGrantedAuthority("ROLE_USER")) // 권한
                    );
            SecurityContextHolder.getContext().setAuthentication(auth);
            HttpSession session = request.getSession();
            session.setAttribute("member", SecurityContextHolder.getContext());
            return "index";
        }

    }


    @PostMapping("/goJoin")
    public String goJoin(
            HttpServletRequest request,
            @RequestParam("privacy_agreed") boolean privacyAgreed,
            @RequestParam("terms_agreed") boolean termsAgreed,
            @RequestParam("marketing_agreed") boolean marketingAgreed,
            Model model
    ) {

        Date today = new Date();
        Timestamp ts = new Timestamp(today.getTime());

        HttpSession session = request.getSession(false);



        if(session != null){
            MemberDto loginInfo = (MemberDto) session.getAttribute("naveruserinfo");
            if(termsAgreed){
                loginInfo.setTerms_agreed(termsAgreed);
                loginInfo.setTerms_agreed_date(ts);
            }else{
                model.addAttribute("needTAgreed", "정책 약관에 동의해주세요.");
            }

            if(privacyAgreed){
                loginInfo.setPrivacy_agreed(privacyAgreed);
                loginInfo.setPrivacy_agreed_date(ts);
            }else{
                model.addAttribute("needPAgreed", "개인정보 약관에 동의해주세요");
            }

            loginInfo.setMarketing_agreed(marketingAgreed);
            loginInfo.setMarketing_agreed_date(ts);

            request.getSession().setAttribute("naveruserinfo", loginInfo);

        }else {
            System.out.println("세션이 없음");
        }

        return "member/join";
    }


    @PostMapping("/insertMember")
    public String insertMember(
            @RequestParam("email") String email,
            @RequestParam("name") String name,
            @RequestParam("nickname") String nickname,
            @RequestParam("phone") String phone,
            @RequestParam(value ="job", required = false, defaultValue = "") String job,
            @RequestParam("address_code") String addressCode,
            @RequestParam("address_1") String address1,
            @RequestParam(value = "profile_msg", required = false, defaultValue = "") String profile_msg,
            @RequestParam(value = "address_2", required = false, defaultValue = "") String address2,
            Model model,
            HttpServletRequest request

    ){
        // memberJoinDto의 email, nickname 조회해서 비어있으면 바로 insert
        // 아니면 모델로 메세지 출력 후 리턴
        MemberJoinDto mjdto = ms.getMemberInfo(email, nickname);
        HttpSession session = request.getSession(false);

        if(mjdto.getEmail().equals(email)){
            model.addAttribute("emailError","이미 존재하는 이메일 입니다");
        }else if(mjdto.getNickname().equals(nickname)){
            model.addAttribute("nicknameError", "이미 존재하는 닉네임 입니다.");
        }else{

            if(session != null){
                MemberDto mdto = (MemberDto) session.getAttribute("naveruserinfo");
                ms.insertMember(email, name, nickname, job, addressCode, address1, address2, profile_msg, phone, mdto);
                session.removeAttribute("naveruserinfo");
            }

            MemberDto loginInfo = ms.getMember(email);
            if(loginInfo != null){
                request.getSession().setAttribute("member", loginInfo);
            }

        }
        return "redirect:/";
    }


}
