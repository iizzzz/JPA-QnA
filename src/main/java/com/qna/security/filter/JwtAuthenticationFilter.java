package com.qna.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qna.dto.security.LoginDto;
import com.qna.entity.Member;
import com.qna.security.JwtTokenizer;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// Username & Password 기반의 인증을 처리하기위한 상속
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    // 로그인 인증정보를 전달받아 UserDetailsService와 비교하여 인증 여부 판단
    private final AuthenticationManager authenticationManager;

    // 클라이언트가 인증에 성공할 경우 토큰 발급을 위한 DI
    private final JwtTokenizer jwtTokenizer;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenizer jwtTokenizer) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenizer = jwtTokenizer;
    }


    /** 메서드 내부에서 인증 시도 로직
     *  인증 성공 시, 인증된 Authentication 객체 반환
     */

    @Override
    @SneakyThrows // 메서드 선언부에 throws를 정의 안해도 검사된 예외를 throw할수 있게 해주는 어노테이션
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        // LoginDTO 에서 받은 인증 정보의 역직렬화를 위한 객체
        ObjectMapper objectMapper = new ObjectMapper();

        // Object Mapper를 이용한 역직렬화, JSON -> Java 객체
        // ServletInputStream -> LoginDto 클래스의 객체로 역직렬화
        LoginDto loginDto = objectMapper.readValue(request.getInputStream(), LoginDto.class);

        // Username과 Password를 포함한 UsernamePasswordAuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        // DI 받은 AuthenticationManager 에게 인증 처리 위임
        return authenticationManager.authenticate(authenticationToken);
    }

    // 인증 성공 시 호출될 메서드
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws ServletException, IOException {

        // 인증 성공 후 위에서 생성된 인증된 Authentication 객체를 이용해 엔티티 객체를 얻음
        Member member = (Member) authResult.getPrincipal();
        // Access Token 생성
        String accessToken = delegateAccessToken(member);
        // Refresh Token 생성
        String refreshToken = delegateRefreshToken(member);

        // Response Header에 Access Token 추가
        response.setHeader("Authorization", "Bearer " + accessToken);
        // Response Header에 Refresh Token 추가
        response.setHeader("Refresh", refreshToken);

        // SuccessHandler 호출
        this.getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
    }

    // Private Access Token 생성 로직
    private String delegateAccessToken(Member member) {

        // Claims를 생성해 username과 role을 넣음
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", member.getEmail());
        claims.put("roles", member.getRoles());

        // Subject 설정 - 유저의 이메일
        String subject = member.getEmail();

        // Access Token 만료시간 설정
        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getAccessTokenExpirationMinutes());

        // JWT Secret Encoding
        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());

        // 위에서 생성한 리소스들로 토큰 생성
        String accessToken = jwtTokenizer.generateAccessToken(claims, subject, expiration, base64EncodedSecretKey);

        return accessToken;
    }

    // Private Refresh Token 생성 로직
    private String delegateRefreshToken(Member member) {
        String subject = member.getEmail();
        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getRefreshTokenExpirationMinutes());
        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());

        String refreshToken = jwtTokenizer.generateRefreshToken(subject, expiration, base64EncodedSecretKey);

        return refreshToken;
    }
}
