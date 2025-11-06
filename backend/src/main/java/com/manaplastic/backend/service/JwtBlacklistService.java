package com.manaplastic.backend.service;

// Giả sử bạn đã có cấu hình Spring Data Redis

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class JwtBlacklistService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String BLACKLIST_PREFIX = "jwt_blacklist:";

    public void blacklistToken(String jwt, long expirationSeconds) {
        // Chỉ thêm vào blacklist nếu token còn hợp lệ (expirationSeconds > 0)
        if (expirationSeconds > 0) {
            redisTemplate.opsForValue().set(
                    BLACKLIST_PREFIX + jwt,
                    true, // Giá trị đơn giản
                    expirationSeconds,
                    TimeUnit.SECONDS
            );
        }
    }

    // Kiểm tra xem JWT có nằm trong danh sách đen hay không.

    public boolean isBlacklisted(String jwt) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + jwt));
    }
}
