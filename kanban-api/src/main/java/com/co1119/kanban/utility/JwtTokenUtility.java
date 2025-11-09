package com.co1119.kanban.utility;

import java.security.Key;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenUtility {
    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationInMs;

    /**
     * Tokenから指定した情報を取得します。
     *
     * @param <T>
     * @param token
     * @param claimsResolve
     * @return
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolve) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolve.apply(claims);
    }

    /**
     * トークンからユーザ名を取得します。
     * 
     * @param token
     * @return
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * トークンから有効期間を取得します。
     * 
     * @param token
     * @return
     */
    public LocalDate getExpirationDateFromToken(String token) {
        Date date = getClaimFromToken(token, Claims::getExpiration);
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * トークンが有効期限切れであるか判定します。
     * 
     * @param token
     * @return
     */
    public boolean isTokenExpired(String token) {
        final LocalDate expiration = getExpirationDateFromToken(token);
        return expiration.isBefore(LocalDate.now());
    }

    /**
     * ユーザ情報からトークンを生成します。
     * 
     * @param userDetails
     * @return
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername());

    }

    /**
     * トークンを検証します。
     * 
     * @param token
     * @param userDetails
     * @return
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return Objects.equals(userDetails.getUsername(), username) && !isTokenExpired(token);
    }

    /**
     * システムで利用するトークンのシークレットを取得します。
     * 
     * @return
     */
    private Key key() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * トークンに含まれる情報を取得します。
     * 
     * @param token
     * @return
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody();
    }

    /**
     * トークンを生成します。
     * 
     * @param claims
     * @param subject
     * @return
     */
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        long nowMillis = System.currentTimeMillis();

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(nowMillis))
                .setExpiration(new Date(nowMillis + jwtExpirationInMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

}
