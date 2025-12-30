//package com.site.pine.controller;
//
//import com.site.pine.dto.member.MemberDto;
//import com.site.pine.service.LikesService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/like")
//public class LikeController {
//
//    private final LikesService ls;
//
//    @PostMapping("/toggleLike")
//    public LikeResponse toggleLike(
//            @AuthenticationPrincipal MemberDto mdto,
//            @RequestBody LikeRequest request
//    ) {
//        long count = ls.toggleLike(
//                mdto.getId(),
//                request.getTargetType(),
//                request.getTargetId()
//        );
//
//        return new LikeResponse(count);
//    }
//}
//
