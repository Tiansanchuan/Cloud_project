package com.atguigu.cloud.service.impl;

import com.atguigu.cloud.service.JWTService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class JWTServiceImpl implements JWTService {

    @Value("${jwt.ttl-ms}")
    private long ttlMs;

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey key() {
        // HS256 的 key 长度要够（至少 32 字节）
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String getToken(String userId) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setHeaderParam("typ", "JWT") // 可有可无
                .setSubject(userId)           // 也可以用 sub 放 userId
                .claim("userid", userId)      // 后期需要加权限信息
                .setId(UUID.randomUUID().toString()) // jti
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + ttlMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String decodeToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("userid", String.class);
    }

    @Override
    public Boolean verify(String token) {
        if (token == null || token.isBlank()) return false;
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            return false; // 过期
        } catch (JwtException e) {
            return false; // 签名错/格式错/篡改等
        }
    }
}
