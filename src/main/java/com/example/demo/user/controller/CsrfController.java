package com.example.demo.user.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfController {

    // CSRF 쪽지(XSRF-TOKEN 쿠키)를 내려주는 문
    // React는 앱 켤 때 이걸 한 번 부르고, 그 뒤 POST마다 쿠키 값을 X-XSRF-TOKEN 헤더에 넣어 보냄
    @GetMapping("/csrf")
    public void csrf(CsrfToken token){
        token.getToken(); // 쪽지를 한 번 꺼내 봐야 응답에 XSRF-TOKEN 쿠키가 실림
    }
}
