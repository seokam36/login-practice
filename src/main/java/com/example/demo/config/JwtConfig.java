package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class JwtConfig {

    // .env의 JWT_SECRET 값 (application.properties의 app.jwt.secret을 거쳐서 옴)
    @Value("${app.jwt.secret}")
    private String secret;

    // 비밀키 글자 → 도장용 열쇠
    @Bean
    public SecretKey jwtSecretKey(){
        byte[] bytes = Base64.getDecoder().decode(secret);
        return new SecretKeySpec(bytes, "HmacSHA256");
    }

    // 도장 찍는 기계 (로그인 성공 때 표 만들때 쓰인다)
    @Bean
    public JwtEncoder jwtEncoder(SecretKey jwtSecretKey){
        // NimbusJwtEncoder → 도장 찍는 기계 (Nimbus는 실제로 일하는 라이브러리 이름)
        // .withSecretKey(jwtSecretKey) → 이 열쇠를 써
        return NimbusJwtEncoder.withSecretKey(jwtSecretKey).build();
    }

    // 도장 확인하는 기계 (경비원이 요청마다 표 검사할 때 쓰인다)
    @Bean
    public JwtDecoder jwtDecoder(SecretKey jwtSecretKey){
        // NimbusJwtDecoder → 도장 확인 기계
        // .macAlgorithm(MacAlgorithm.HS256) → 받을 도장 방식을 HS256으로 고정
        return NimbusJwtDecoder.withSecretKey(jwtSecretKey).macAlgorithm(MacAlgorithm.HS256).build();
    }
}
