package com.qna.security.filter;

import com.qna.security.CustomAuthorityUtils;
import com.qna.security.JwtTokenizer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

// Request당 1번만 실행되는 Security Filter 상속
public class JwtVerificationFilter extends OncePerRequestFilter {

    private final JwtTokenizer jwtTokenizer;
    private final CustomAuthorityUtils authorityUtils;

    // JwtTokenizer =  JWT 검증 + Claims를 얻는데 사용
    // CustomAuthorityUtils = JWT 검증에 성공하면 Authentication 객체에 채울 유저의 권한 생성하는데 사용
    public JwtVerificationFilter(JwtTokenizer jwtTokenizer, CustomAuthorityUtils authorityUtils) {
        this.jwtTokenizer = jwtTokenizer;
        this.authorityUtils = authorityUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // JWT 검증 Private Method
        Map<String, Object> claims = verifyJws(request);

        setAuthenticationToContext(claims);

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        String authorization = request.getHeader("Authorization");

        return authorization == null | !authorization.startsWith("Bearer");
    }

    /** [JWT 검증 Private Method]
     *  jws = request의 Header에서 JWT를 얻고 replace()를 이용해 Bearer 부분 삭제
     *  base64EncodedSecretKey = JWT 서명을 검증하기 위한 Secret Key
     *  claims = JWT에서 Claims 파싱.
     *  * 파싱에 성공 == 내부적으로 서명 검증에 성공했다는 말과 동일함
     */
    private Map<String, Object> verifyJws(HttpServletRequest request) {
        String jws = request.getHeader("Authorization").replace("Bearer ", ""); // (3-1)
        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey()); // (3-2)
        Map<String, Object> claims = jwtTokenizer.getClaims(jws, base64EncodedSecretKey).getBody();   // (3-3)

        return claims;
    }

    /** [Authentication 객체를 SecurityContext에 저장하기 위한 Private Method]
     * username = 파싱한 claims에서 username을 얻음
     * authorities = claims에서 얻은 권한정보를 기반으로 List<GrantedAuthority> 생성
     * authentication = username과 GrantedAuthority를 포함해 Authentication 객체 생성
     * SecurityContext에 Authentication 저장
     */
    private void setAuthenticationToContext(Map<String, Object> claims) {
        String username = (String) claims.get("username");
        List<GrantedAuthority> authorities = authorityUtils.createAuthorities((List)claims.get("roles"));
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}