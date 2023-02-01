package com.qna.security.filter;

import com.qna.security.CustomAuthorityUtils;
import com.qna.security.JwtTokenizer;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
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

/**
 * Request당 1번만 실행되는 Security Filter 상속
 * 인증과 관련된 Filter는 성공 & 실패 단 한번만 판단하면 되므로,
 * 한 번만 실행되는 OncePerRequestFilter를 사용하는게 적합함
 */
public class JwtVerificationFilter extends OncePerRequestFilter {

    // JWT를 검증하고 Claims(토큰에 포함된 정보)를 얻는데 사용됨
    private final JwtTokenizer jwtTokenizer;
    // JWT 검증에 성공하면 Authentication 객체에 채울 사용자의 권한을 생성하는데 사용
    private final CustomAuthorityUtils authorityUtils;

    // JwtTokenizer =  JWT 검증 + Claims를 얻는데 사용
    // CustomAuthorityUtils = JWT 검증에 성공하면 Authentication 객체에 채울 유저의 권한 생성하는데 사용
    public JwtVerificationFilter(JwtTokenizer jwtTokenizer, CustomAuthorityUtils authorityUtils) {
        this.jwtTokenizer = jwtTokenizer;
        this.authorityUtils = authorityUtils;
    }

    /**
     * 아래의 두 검증 메소드를 통과한 후 doFilter 메소드로 다음 필터로 넘어감
     * AuthenticationEntryPoint에서 사용될 예외 로직 추가
     * JWT에 대한 서명 검증이 실패할 경우 throw 되는 SignatureException에 대한 처리가 없으므로 예외 로직 추가
     * JWT가 만료될 경우 발생하는 ExpiredJwtException에 대한 예외 처리 로직 추가
     * Exception을 catch 하고 다시 throw 하지 않고 단순이 setAttribute만 하는 이유
     * 이곳의 예외 로직은클라이언트의 인증정보가 Context에 저장되지 않게 하는것이며,
     * 저장되지 않으면 AuthenticationException이 발생하고 이걸 EntryPoint 클래스에서 처리하게 됨
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            Map<String, Object> claims = verifyJws(request);
            setAuthenticationToContext(claims);
        } catch (SignatureException se) {
            request.setAttribute("exception", se);
        } catch (ExpiredJwtException ee) {
            request.setAttribute("exception", ee);
        } catch (Exception e) {
            request.setAttribute("exception", e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 1. Authorization Header의 값을 얻음
     * 2. 헤더의 값이 null 이거나 Bearer로 시작하지 않으면 해당 Filter 동작 수행하지 않게 함
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        String authorization = request.getHeader("Authorization");

        return authorization == null || !authorization.startsWith("Bearer");
    }

    /** [JWT 검증 Private Method]
     *  jws = request의 Header에서 JWT를 얻고 replace()를 이용해 Bearer 부분 삭제
     *  여기서의 JWT는 response header로 전달받은 JWT를 request header에 추가해서 서버측에 전송한 JWT임
     *  base64EncodedSecretKey = JWT 서명(Signature)을 검증하기 위한 Secret Key(JWT 자체를 검증함)
     *  claims = JWT에서 Claims 파싱.
     *  * 파싱에 성공 == 내부적으로 서명 검증에 성공했다는 말과 동일함
     *  그러므로 verify()같은 메서드가 필요없고 정상 파싱되면 서명 검증도 자연스럽게 성공했다고 기억하면됨
     */
    private Map<String, Object> verifyJws(HttpServletRequest request) {
        String jws = request.getHeader("Authorization").replace("Bearer ", "");
        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());
        Map<String, Object> claims = jwtTokenizer.getClaims(jws, base64EncodedSecretKey).getBody();

        return claims;
    }

    /** [Authentication 객체를 SecurityContext에 저장하기 위한 Private Method]
     * username = 파싱한 JWT의 claims에서 username을 얻음
     * authorities = claims에서 얻은 권한정보를 기반으로 List<GrantedAuthority> 권한 생성
     * authentication = username과 GrantedAuthority 권한을 포함해 Authentication 객체 생성
     * SecurityContext에 Authentication 저장
     */
    private void setAuthenticationToContext(Map<String, Object> claims) {
        String username = (String) claims.get("username");
        List<GrantedAuthority> authorities = authorityUtils.createAuthorities((List)claims.get("roles"));
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}