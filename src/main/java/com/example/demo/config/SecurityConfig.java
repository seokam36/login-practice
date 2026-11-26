package com.example.demo.config;

import jakarta.servlet.http.Cookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                // CSRF 방어 끄기
                .csrf(csrf -> csrf.disable())

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
                        auth.requestMatchers("/auth/signup","/auth/login","/error").permitAll().anyRequest().authenticated())
                // // JWT 검사 켜기 → 요청마다 BearerTokenResolver로 토큰 꺼내고, JwtDecoder로 도장 확인
                .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));
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
}
