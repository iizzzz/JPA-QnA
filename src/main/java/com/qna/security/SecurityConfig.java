package com.qna.security;

import com.qna.security.filter.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    /**
     * Session Creation Policy를 위한 CustomAuthorityUtils 추가
     * AutenticationEntryPoint & AccessDeniedHandler 추가
     */

    private final JwtTokenizer jwtTokenizer;
    private final CustomAuthorityUtils authorityUtils;

    public SecurityConfig(JwtTokenizer jwtTokenizer, CustomAuthorityUtils authorityUtils) {
        this.jwtTokenizer = jwtTokenizer;
        this.authorityUtils = authorityUtils;
    }

    /*
     * .apply() -> Custom Configuration 적용
     * --- sessionManagement()에 대한 설명 ---
     * SessionCreationPolicy.ALWAYS - 항상 세션 생성
     * SessionCreationPolicy.NEVER - 세션을 생성하지 않지만 이미 생성된게 있다면 사용
     * SessionCreationPolicy.IF_REQUIRED - 필요한 경우만 세션 생성
     * SessionCreationPolicy.STATELESS - 세션을 생성하지 않으며, SecurityContext 정보를 얻기위해, 세션 사용 X
     * --- authorizeHttpRequests 설명 ---
     * URL 별로 접근 권한 부여
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .headers().frameOptions().sameOrigin()
                .and()
                .csrf().disable()
                .cors(withDefaults())
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                // Exception Handling - EntryPoint, AccessDenied Handler 추가
                .exceptionHandling()
                .authenticationEntryPoint(new MemberAuthenticationEntryPoint())
                .accessDeniedHandler(new MemberAccessDeniedHandler())
                .and()
                // Custom Configuration 추가
                .apply(new CustomFilterConfigurer())
                .and()
                // URL 별로 접근 권한 부여
                .authorizeHttpRequests(authorize -> authorize
                                .antMatchers(HttpMethod.POST, "/*/members").permitAll()
                                .antMatchers(HttpMethod.PATCH, "/*/members").hasRole("USER")
                                .antMatchers(HttpMethod.GET, "/*/members").hasRole("ADMIN")
                                .antMatchers(HttpMethod.GET, "/*/members/**").hasAnyRole("USER", "ADMIN")
                                .antMatchers(HttpMethod.DELETE, "/*/members/**").hasRole("USER")
                                .anyRequest().permitAll()
                );
        return http.build();
    }

    // PasswordEncoder 설정
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // CORS 설정
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PATCH", "DELETE"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /** JWT AuthenticationFilter 등록
     * AbstractHttpConfigurer<CustomFilterConfigurer, HttpSecurity> 를 이용해 Custom Configurer 구현 가능
     * 파라미터로는 <상속하는타입, HttpSecurityBuilder를 상속하는 타입을 제네릭으로 지정>
     */
    public class CustomFilterConfigurer extends AbstractHttpConfigurer<CustomFilterConfigurer, HttpSecurity> {

        // Configure Method 를 Override 함으로써 SecurityConfig 를 Customizing 할 수 있다
        @Override
        public void configure(HttpSecurity builder) throws Exception {

            // getSharedObject()로 인해 SecurityConfigurer 간에 공유되는 객체를 얻을 수 있음
            // AuthenticationManager 객체를 얻음
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

            // JwtAuthenticationFilter 객체 생성과 동시에,
            // AuthenticationManager & JwtTokenizer를 DI 해줌
            JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, jwtTokenizer);

            // setFilterProccessUrl() 를 통해 로그인 URL Customizing (default url은 /login 이다)
            jwtAuthenticationFilter.setFilterProcessesUrl("/auth/login");
            // Success, Failure Handler 추가
            jwtAuthenticationFilter.setAuthenticationSuccessHandler(new MemberAuthenticationSuccessHandler());
            jwtAuthenticationFilter.setAuthenticationFailureHandler(new MemberAuthenticationFailureHandler());

            // JWT를 생성할 때, JwtVerificationFilter 에서 사용되는 객체들을 DI
            JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(jwtTokenizer, authorityUtils);

            // addFilter()를 통해 JWT 검증필터를 Spring Security Filter Chain에 추가
            // addFilter()를 JwtAuthenticationFilter가 수행된 바로 다음에 동작하도록 추가
            builder.addFilter(jwtAuthenticationFilter)
                    .addFilterAfter(jwtVerificationFilter, JwtAuthenticationFilter.class);
        }
    }
}