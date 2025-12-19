package com.site.pine.controller;

import com.site.pine.dto.community.PostListDto;
import com.site.pine.dto.community.PostResDto;
import com.site.pine.dto.member.CountryDto;
import com.site.pine.dto.member.MemberDto;
import com.site.pine.dto.member.MemberJoinDto;
import com.site.pine.entity.Member;
import com.site.pine.repository.MemberRepository;
import com.site.pine.security.AuthUtil;
import com.site.pine.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
                + "&state=" + state
                + "&scope=email";

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
        String accessToken = null;
        try{
            accessToken = new JSONObject(tokenRes.getBody()).getString("access_token");
        }catch(Exception e){
            System.out.println("access_token 에 문제발생");
            return "redirect:/";
        }

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

        MemberDto member = ms.findByEmail(email);

        if(member == null){
            MemberDto mdto = new MemberDto();
            mdto.setEmail(profile.optString("email"));
            mdto.setName(profile.optString("name"));
            mdto.setProfileimg(profile.optString("profile_image"));
            mdto.setPhone(profile.optString("mobile"));
            mdto.setProvider(1);
            request.getSession().setAttribute("userinfo", mdto);

            System.out.println("네이버 콜백 완료됨");

            return "member/jointerms";
        }else{
            member = ms.getMember(email);

            if(member.getProvider() != 1) {
                member.setProvider(1);
            }

            AuthUtil.login(member, request.getSession());


            return "redirect:/";
        }

    }


    @GetMapping("/googleLoginSuccess")
    public String loginSuccess(@AuthenticationPrincipal OAuth2User principal, HttpSession session, HttpServletRequest request) {
        String email = principal.getAttribute("email");
        String name = principal.getAttribute("name");

        System.out.println("principal: " + principal);

        MemberDto member = ms.findByEmail(email);
        if(member == null){
            // 신규 회원 처리, 세션 저장
            MemberDto mdto = new MemberDto();
            mdto.setEmail(email);
            mdto.setName(name);
            mdto.setProvider(2);
            session.setAttribute("userinfo", mdto);
            return "member/jointerms";
        } else {
            // 기존 회원 로그인
            member = ms.getMember(email);
            AuthUtil.login(member, request.getSession());
            return "redirect:/";
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

        HttpSession session = request.getSession(false);
        if(session == null || session.getAttribute("userinfo") == null){
            // 세션 없으면 로그인 페이지로 이동
            return "redirect:/";
        }

        MemberDto loginInfo = (MemberDto) session.getAttribute("userinfo");
        Date today = new Date();
        Timestamp ts = new Timestamp(today.getTime());

        boolean hasError = false;

        if(!termsAgreed){
            model.addAttribute("needTAgreed", "정책 약관에 동의해주세요.");
            hasError = true;
        } else {
            loginInfo.setTerms_agreed(true);
            loginInfo.setTerms_agreed_date(ts);
        }

        if(!privacyAgreed){
            model.addAttribute("needPAgreed", "개인정보 약관에 동의해주세요.");
            hasError = true;
        } else {
            loginInfo.setPrivacy_agreed(true);
            loginInfo.setPrivacy_agreed_date(ts);
        }

        // 마케팅 약관 optional
        loginInfo.setMarketing_agreed(marketingAgreed);
        loginInfo.setMarketing_agreed_date(ts);

        // 약관 미동의 시 다시 jointerms page
        if(hasError){
            return "member/jointerms";
        }

        // 세션 갱신
        session.setAttribute("userinfo", loginInfo);

        System.out.println("약관동의 완료됨");

        // 여기서 insertMember form으로 redirect 혹은 다음 단계로 이동
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
        HttpSession session = request.getSession(false);

        if(session == null || session.getAttribute("userinfo") == null){
            // 세션 없으면 강제 redirect
            return "member/jointerms";
        }

        MemberJoinDto mjdto = ms.getMemberInfo(email, nickname);

        if(mjdto.getEmail().equals(email)){
            model.addAttribute("emailError","이미 존재하는 이메일 입니다");
            return "member/join";
        }else if(mjdto.getNickname().equals(nickname)){
            model.addAttribute("nicknameError", "이미 존재하는 닉네임 입니다.");
            return "member/join";
        }else {

            if (session != null) {
                MemberDto mdto = (MemberDto) session.getAttribute("userinfo");
                ms.insertMember(email, name, nickname, job, addressCode, address1, address2, profile_msg, phone, mdto);
                session.removeAttribute("userinfo");
            }

            MemberDto loginInfo = ms.getMember(email);
            if (loginInfo != null) {
                AuthUtil.login(loginInfo, request.getSession());
            }
        }
        return "redirect:/";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/GoMypage")
    public String goMypage(@AuthenticationPrincipal MemberDto mdto, Model model){
        model.addAttribute("member", mdto);
        System.out.println("mdto : "+mdto);
        List<PostListDto> myPost = ms.getPostList(mdto.getId());
        model.addAttribute("post", myPost);
        return "member/mypage";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/GoEditProfile")
    public String goEditProfile(){
        return "member/editprofile";
    }


    @GetMapping("/countrySearch")
    @ResponseBody
    public List<CountryDto> goCountrySearch(@RequestParam String keyword){
        return ms.searchCountry(keyword);
    }


}
