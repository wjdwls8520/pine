package com.site.pine.controller;

import com.site.pine.entity.Member;
import com.site.pine.repository.MemberRepository;
import com.site.pine.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
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

    @GetMapping("/auth/naver/callback")
    public String callback(@RequestParam String code,
                           @RequestParam String state,
                           HttpServletRequest request)  {

        // 1) 토큰 요청
        String tokenUrl = "https://nid.naver.com/oauth2.0/token";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("state", state);

        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(params);
        ResponseEntity<String> tokenRes = rt.postForEntity(tokenUrl, req, String.class);

        // 2) access_token으로 사용자 정보 가져오기
        String accessToken = new JSONObject(tokenRes.getBody()).getString("access_token");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        ResponseEntity<String> userRes = rt.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        // 3) 사용자 정보 DB 처리
        JSONObject profile = new JSONObject(userRes.getBody()).getJSONObject("response");
        String email = profile.getString("email");

        Member member = mr.findByEmail(email);

        if(member == null){
            return "member/jointerms";
        }


        // memberService.loginOrRegister(email, ...);

        // 4) 세션에 로그인 정보 저장
        request.getSession().setAttribute("loginUserEmail", email);

         //5) JSP 페이지로 이동
        return "redirect:/index.jsp";  // 메인 페이지로 이동
    }


}
