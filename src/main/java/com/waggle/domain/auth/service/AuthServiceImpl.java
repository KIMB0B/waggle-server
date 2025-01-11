package com.waggle.domain.auth.service;

import com.waggle.domain.auth.dto.*;
import com.waggle.domain.auth.entity.RefreshToken;
import com.waggle.domain.auth.repository.RefreshTokenRepository;
import com.waggle.global.exception.JwtTokenException;
import com.waggle.global.response.ApiStatus;
import com.waggle.global.secure.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    @Value("${JWT_ACCESS_TOKEN_EXPIRE_TIME}")
    private long ACCESS_TOKEN_EXPIRATION_TIME; // 액세스 토큰 유효기간

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    @Override
    public AccessTokenResponse reissueAccessToken(String refreshToken) {
        String userId = jwtUtil.getUserIdFromToken(refreshToken);
        RefreshToken existRefreshToken = refreshTokenRepository.findByUserId(UUID.fromString(userId));
        String accessToken = null;

        if (existRefreshToken == null || !existRefreshToken.getToken().equals(refreshToken) || jwtUtil.isTokenExpired(refreshToken)) {
            // 리프레쉬 토큰이 다르거나, 만료된 경우
            throw new JwtTokenException(ApiStatus._INVALID_REFRESH_TOKEN);
        } else {
            // 액세스 토큰 재발급
            accessToken = jwtUtil.generateAccessToken(UUID.fromString(userId), ACCESS_TOKEN_EXPIRATION_TIME);
        }

        return AccessTokenResponse.builder()
                .accessToken(accessToken)
                .build();
    }

    @Override
    public String exchangeTemporaryToken(String temporaryToken) {
        String key = "TEMP_TOKEN:" + temporaryToken;
        String accessToken = redisTemplate.opsForValue().get(key);
        
        if (accessToken == null) {
            throw new JwtTokenException(ApiStatus._INVALID_TEMPORARY_TOKEN);
        }
        
        // 임시 토큰 삭제
        redisTemplate.delete(key);
        
        return accessToken;
    }

    @Override
    public void deleteRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            throw new JwtTokenException(ApiStatus._REFRESH_TOKEN_NOT_FOUND);
        }
    
        String userId = jwtUtil.getUserIdFromToken(refreshToken);
        refreshTokenRepository.deleteByUserId(UUID.fromString(userId));
    }
}