package com.zinikai.shop.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.swing.text.StyledEditorKit;
import java.util.Date;

@Component
public class JwtUtil {

    //실제 서비스에서는 환경 변수로 관리
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256); // HS256을 위한 키 생성

    //토큰생성
    public String generateToken(String email){
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((System.currentTimeMillis() + 1000 * 60 * 60 * 10))) //유효기간 10시간
                .signWith(SECRET_KEY)
                .compact();
    }

    // 토큰에서 이메일 추출
    public String extractEmail(String token){
        return extractAllClaims(token).getSubject();
    }

    //토큰 유효성 검사
    public boolean validateToken(String token , String email){
        final String extractEmail = extractEmail(token);
        return (email.equals(extractEmail) && !isTokenExpired(token));
    }


    // 최신버전 x

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

}
