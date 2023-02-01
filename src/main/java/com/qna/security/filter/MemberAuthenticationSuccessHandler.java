package com.qna.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qna.entity.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 인증 성공 시 추가작업을 할 수 있는 클래스
 */

@Slf4j
public class MemberAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Member member = (Member) authentication.getPrincipal();

        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> loginResponse = new LinkedHashMap<>();
        loginResponse.put("memberId", member.getMemberId());
        loginResponse.put("email", member.getEmail());
        loginResponse.put("name", member.getName());
        loginResponse.put("roles", member.getRoles());

        String body = objectMapper.writeValueAsString(loginResponse);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(body);

        log.info("# Authentication Successfully!");
        log.info("name: {}, email: {}, roles: {}", member.getName(), member.getEmail(), member.getRoles());
    }
}
