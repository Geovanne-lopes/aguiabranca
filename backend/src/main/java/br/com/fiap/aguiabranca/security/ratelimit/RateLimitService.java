package br.com.fiap.aguiabranca.security.ratelimit;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import br.com.fiap.aguiabranca.common.exception.ApiException;

@Service
public class RateLimitService {

    private final Cache<String, AtomicInteger> loginAttempts = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(10))
            .build();
    private final Cache<String, AtomicInteger> registerAttempts = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(1))
            .build();
    private final Cache<String, AtomicInteger> resetAttempts = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(1))
            .build();
    private final Cache<String, AtomicInteger> aiGenerations = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(10))
            .build();

    public void checkLogin(String ip, String email) {
        consume("login:" + ip + ":" + normalize(email), loginAttempts, 10);
    }

    public void checkRegister(String ip) {
        consume("register:" + ip, registerAttempts, 10);
    }

    public void checkResetPassword(String ip) {
        consume("reset:" + ip, resetAttempts, 5);
    }

    public void checkAiGeneration(String userId) {
        consume("ai:" + userId, aiGenerations, 5);
    }

    private void consume(String key, Cache<String, AtomicInteger> cache, int limit) {
        AtomicInteger counter = cache.get(key, ignored -> new AtomicInteger(0));
        if (counter.incrementAndGet() > limit) {
            throw ApiException.rateLimited();
        }
    }

    private String normalize(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
