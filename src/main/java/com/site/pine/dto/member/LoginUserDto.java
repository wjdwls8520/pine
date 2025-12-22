//package com.site.pine.dto.member;
//
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//
//@Getter
//@AllArgsConstructor
//public class LoginUserDto {
//
//    private String email;
//    private String name;
//    private String nickname;
//    private String phone;
//    private String country;
//
//    public static LoginUserDto from(Object principal) {
//
//        if (principal instanceof CustomUserDetails user) {
//            return new LoginUserDto(
//                    user.getEmail(),
//                    user.getName(),
//                    user.getNickname(),
//                    user.getPhone(),
//                    user.getCountry()
//            );
//        }
//
//        if (principal instanceof DefaultOAuth2User oauth) {
//            Map<String, Object> attr = oauth.getAttributes();
//
//            return new LoginUserDto(
//                    (String) attr.get("email"),
//                    (String) attr.getOrDefault("name", ""),
//                    "",
//                    "",
//                    ""
//            );
//        }
//
//        throw new IllegalStateException("Unknown principal type");
//
//}
