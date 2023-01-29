package com.qna.security;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

// 유형별 권한 생성
@Component
public class CustomAuthorityUtils {

    @Value("${mail.address.admin}")
    private String adminMailAddress;

//    // 메모리 저장용
//    private final List<GrantedAuthority> ADMIN_ROLES = AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER");
//    private final List<GrantedAuthority> USER_ROLES = AuthorityUtils.createAuthorityList("ROLE_USER");

    // DB 저장용
    private final List<String> ADMIN = List.of("ADMIN", "USER");
    private final List<String> USER = List.of("USER");



//    // 메모리 상의 Role 기반으로 권한 생성
//    public List<GrantedAuthority> createAuthorities(String email) {
//
//        if (email.equals(adminMailAddress)) {
//            return ADMIN_ROLES;
//        }
//        return USER_ROLES;
//    }

    // DB에 저장된 Role을 기반으로 권한 정보 생성
    public List<GrantedAuthority> createAuthorities(List<String> roles) {

        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_"+role))
                .collect(Collectors.toList());
        return authorities;
    }

    // DB 저장용 Role 생성 검증 요소는 알아서 필드 선정
    public List<String> createRoles(String email) {

        if (email.equals(adminMailAddress)) {
            return ADMIN;
        }
        return USER;
    }
}