package com.example.demo.config;

import jakarta.servlet.http.Cookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder, BearerTokenResolver resolver) throws Exception{
        http
                // // CSRF 방어 켜기 (React 같은 SPA용)
                .csrf(csrf -> csrf.spa())

                // 서버가 기억하지 않기 -> 토큰 방식이라 세션에 쓰지 말기
                .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 기본 로그인 화면 끄기 -> 스프링 시큐리티 아무 설정 안하면 자기가 만든 로그인 화면을 띄워준다
                .formLogin(form -> form.disable())

                // 팝업 로그인 끄기 -> 브라우저 위에 팝업 창으로 "아이디/비번 입력하세요" 라고 뜨게하는 옛날 방식이라 끄기
                .httpBasic(basic -> basic.disable())

                // 경비원 규칙, 어느 문을 열고 잠글지
                // .requestMatchers("/auth/signup", "/auth/login").permitAll() -> 회원가입이랑 로그인 오는 사람은 통과
                // ..anyRequest().authenticated() -> 그 외 모든 요청은 로그인한 사람만 통과
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/auth/signup","/auth/login","/error","/csrf").permitAll().anyRequest().authenticated())
                // 로그인 안 한 사람이 잠긴 문에 오면 401
                // (oauth2ResourceServer 세트를 빼면 기본값이 403이라 직접 정해 줌)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                // 검색대(쿠키 JWT 검사)를 경비원 줄에 세움 → 문 규칙 검사(AuthorizationFilter)보다 앞에
                .addFilterBefore(jwtCookieFilter(jwtDecoder, resolver), AuthorizationFilter.class);
        return http.build();
    }

    // 비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // 경비원한테 토큰은 쿠키에 accessToken에 있다고 알려주기
    @Bean
    public BearerTokenResolver bearerTokenResolver(){
        return request -> {
            // 1. 요청에 실려온 쿠키 가져오기
            Cookie[] cookies = request.getCookies();
            if (cookies == null){
                return null;
            }
            // 2. 실려온 쿠키 하나씩 찾아서 "accessToken" 가져오기
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())){
                    return cookie.getValue();
                }
            }
            // 3. 없으면 null
            return null;
        };
    }

    // 쿠키의 JWT를 검사하는 부품 (oauth2ResourceServer 세트 대신 단품으로 조립)
    // 세트엔 "토큰 있으면 CSRF 면제"가 딸려 와서, 로그인한 사람 요청이 CSRF 검사를 안 받게 됨
    private BearerTokenAuthenticationFilter jwtCookieFilter(JwtDecoder jwtDecoder, BearerTokenResolver resolver){
        // 1. 담당자 : JwtDecoder로 도장이 진짜인지 확인
        JwtAuthenticationProvider provider = new JwtAuthenticationProvider(jwtDecoder);

        // 2. 검사 부품 : 토큰을 창구(ProviderManager)에 넘겨서 담당자한테 확인받음
        BearerTokenAuthenticationFilter filter = new BearerTokenAuthenticationFilter(new ProviderManager(provider));

        // 3. 토큰은 쿠키에서 꺼내라고 알려 줌 (위에 만든 bearerTokenResolver)
        filter.setBearerTokenResolver(resolver);

        return filter;
    }
    /*
    1. 확인하는 사람 뽑고 → 도장 기계 쥐여 줌
    2. 부스에 앉힘
    3. 검색대 세우고 → 부스랑 연결
    4. 검색대한테 "탑승권은 쿠키에서 꺼내" 알려 줌
    5. 완성된 검색대 돌려줌
    */
}
