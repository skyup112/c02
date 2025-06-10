package com.example.b03.security.service;//package com.example.b03.security.service;
//
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//
//@Service
//public class UserService {
//
//    public Long getLoggedInMemberNo() {
//        // SecurityContextHolder에서 현재 로그인된 사용자 정보를 가져옴
//        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        // CustomUserDetails로 캐스팅 후, memberNo를 가져옴
//        if (userDetails instanceof CustomUserDetails) {
//            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
//            return customUserDetails.getMember().getMemberNo();  // Member 객체에서 memberNo를 가져옴
//        }
//
//        throw new RuntimeException("No user is authenticated");
//    }
//}
