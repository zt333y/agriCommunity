package com.example.agricommunity.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtils {
    // 秘钥，实际企业开发中会很复杂，咱们这里设一个长字符串即可
    private static final String SECRET_KEY = "AgriCommunity_SecretKey_MustBeLongEnough";
    // Token 有效期设为 24 小时
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    /**
     * 1. 登录成功时，生成 Token
     */
    public static String generateToken(Long userId, String username, Integer roleType) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("username", username)
                .claim("roleType", roleType) // 把用户的核心身份信息存入 token
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    /**
     * 2. 拦截请求时，解析 Token
     */
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}