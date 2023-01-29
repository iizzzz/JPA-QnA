package com.qna.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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

    private final JwtTokenizer jwtTokenizer;

    public SecurityConfig(JwtTokenizer jwtTokenizer) {
        this.jwtTokenizer = jwtTokenizer;
    }

    // HTTP 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .headers().frameOptions().sameOrigin()
                .and()
                .csrf().disable()
                .cors(withDefaults())
                .formLogin().disable()
                .httpBasic().disable()
                .apply(new CustomFilterConfigurer()) // JWT 로그인 설정
                .and()
                .authorizeHttpRequests(authorize -> authorize
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

    // JWT AuthenticationFilter 등록
    public class CustomFilterConfigurer extends AbstractHttpConfigurer<CustomFilterConfigurer, HttpSecurity> {

        @Override // Configure Method를 Override 함으로써 SecurityConfig를 Customizing 할 수 있다
        public void configure(HttpSecurity builder) throws Exception {

            // getSharedObject()로 인해 SecurityConfigurer간에 공유되는 객체를 얻을 수 있음
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

            // 객체 생성과 동시에 AuthenticationManager & JwtTokenizer를 DI 해줌
            JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, jwtTokenizer);

            // setFilterProccessUrl() 를 통해 로그인 URL Customize 가능 (default url은 /login 이다)
            jwtAuthenticationFilter.setFilterProcessesUrl("/auth/login");

            // addFilter()를 통해 JWT 검증필터를 Filter Chain에 추가
            builder.addFilter(jwtAuthenticationFilter);
        }
    }
}