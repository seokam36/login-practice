package com.example.demo.user.controller;

import com.example.demo.user.dto.LoginRequest;
import com.example.demo.user.dto.SignupRequest;
import com.example.demo.user.dto.UserResponse;
import com.example.demo.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    @Value("${jwt.access-token-time}")
    private Duration accessTokenTime;

    private final UserService userService;

    @PostMapping("/signup")
    public UserResponse signup(@Valid @RequestBody SignupRequest request){
        return userService.signup(request);
    }

    // ResponseEntity → HTTP 응답을 직접 만든다 (상태코드 + 헤더 + 본문) , <Void> → 본문 비어있게
    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest request){
        // 1. 로그인 확인되면 accessToken 받아오기
        String token = userService.login(request);

        // 2. 받아온 accessToken 쿠키에 넣기
        ResponseCookie cookie = ResponseCookie.from("accessToken", token)
                .httpOnly(true)             // JS에서 못꺼내기
                .secure(false)              // 지금은 http라 false **(https 할땐 true)**
                .path("/")                  // 모든 주소 요청에 같이 보냄
                .sameSite("Lax")            // 다른 사이트에서 몰래 보내는 요청엔 안 실리게
                .maxAge(accessTokenTime)    // 15분
                .build();

        // 3. ResponseEntity 파츠 완성 시키기 → 200(ok), 헤더에는 쿠키, 본문 X
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }
}
