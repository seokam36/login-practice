package com.example.demo.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TokenService {

    // JwtConfig에서 만든 도장 찍는 기계
    private final JwtEncoder jwtEncoder;

    // access 토큰 유효 시간 (application.properties의 15m)
    @Value("${jwt.access-token-time}")
    private Duration accessTokenTime;

    // 회원 번호와 아이디를 받아서 JWT 글자를 돌려줌
    public String createAccessToken(Long userId, String userName){
        Instant now = Instant.now();

        // 토큰에 적을 내용
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(String.valueOf(userId))        // sub : 누구인지 (회원 번호)
                .claim("userName", userName)      // 덤으로 아이디
                .issuedAt(now)                          // iat : 언제 발급했는지
                .expiresAt(now.plus(accessTokenTime))   // exp : 언제까지 유효한지 (15분 뒤)
                .build();

        // 헤더 : HS256으로 찍었다고 적는다
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        // 기계로 도장 찍고 → 글자로 꺼내서 돌려줌
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
