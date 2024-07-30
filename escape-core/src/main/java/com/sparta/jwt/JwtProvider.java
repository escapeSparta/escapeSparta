package com.sparta.jwt;

import com.sparta.domain.user.entity.UserType;
import com.sparta.global.exception.customException.CustomSecurityException;
import com.sparta.global.exception.errorCode.SecurityErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j(topic = "JwtProvider")
@Component
@RequiredArgsConstructor
public class JwtProvider {

    // Header KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // 리프레시 헤더 값
    public static final String REFRESH_HEADER = "RefreshToken";
    // 사용자 권한 값의 KEY
    public static final String AUTHORIZATION_KEY = "auth";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";
    // 토큰 만료시간 (30분)
    public static final long ACCESS_TOKEN_TIME = 30 * 60 * 1000L;

    // 1분
//    public static final long ACCESS_TOKEN_TIME = 60 * 1000L;

    // 리프레시 토큰 만료시간 (7일)
    public static final long REFRESH_TOKEN_TIME = 7 * 24 * 60 * 60 * 1000L;

    // JWT secret key
    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] accessKeyBytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(accessKeyBytes);
    }

    // 토큰 생성
    public String createToken(String userEmail, long expiredTime, UserType role) {
        Date date = new Date();

        return BEARER_PREFIX + Jwts.builder()
                .setSubject(userEmail)
                .claim(AUTHORIZATION_KEY, role)
                .setExpiration(new Date(date.getTime() + expiredTime))
                .setIssuedAt(date)
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    // header에서 JWT value 가져오기
    public String getJwtFromHeader(HttpServletRequest request, String header) {
        String bearerToken = request.getHeader(header);
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    public boolean validateTokenInternal(HttpServletRequest request, String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("Invalid JWT signature, 유효하지 않은 JWT 서명 입니다.");
            request.setAttribute("exception", new CustomSecurityException(SecurityErrorCode.INVALID_TOKEN));
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.");
            request.setAttribute("exception", new CustomSecurityException(SecurityErrorCode.EXPIRED_TOKEN));
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
            request.setAttribute("exception", new CustomSecurityException(SecurityErrorCode.INVALID_TOKEN));
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
            request.setAttribute("exception", new CustomSecurityException(SecurityErrorCode.INVALID_TOKEN));
        }
        return false;
    }

    public boolean isExpiredAccessToken(String accessToken){
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken);
        } catch(ExpiredJwtException e) {
            return true;
        }
        return false;
    }

    // 토큰에서 Claims 불러오기
    public Claims getUserInfoFromClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}