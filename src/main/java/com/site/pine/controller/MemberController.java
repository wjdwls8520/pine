package com.site.pine.controller;

import com.site.pine.entity.Member;
import com.site.pine.repository.MemberRepository;
import com.site.pine.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
            Map<String, Object> naveruserinfo = new HashMap<>();
            naveruserinfo.put("email", profile.optString("email"));
            naveruserinfo.put("name", profile.optString("name"));
            naveruserinfo.put("profile_image", profile.optString("profile_image"));
            naveruserinfo.put("phone", profile.optString("mobile"));
            naveruserinfo.put("provider", "NAVER");
            request.getSession().setAttribute("naveruserinfo", naveruserinfo);
            return "member/jointerms";
        }else{
            request.getSession().setAttribute("member", member);
            return "index";
        }

    }


    @GetMapping("/goJoin")
    public String goJoin(HttpServletRequest request) {

        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(today);

        HttpSession session = request.getSession(false);

        if(session != null){
            Map<String , Object> loginInfo = (Map<String, Object>)session.getAttribute("naveruserinfo");
            loginInfo.put("terms_agreed", 1);
            loginInfo.put("terms_agreed_date", formattedDate);

            request.getSession().setAttribute("naveruserinfo", loginInfo);

        }else {
            System.out.println("세션이 없음");
        }

        return "member/join";
    }


}
