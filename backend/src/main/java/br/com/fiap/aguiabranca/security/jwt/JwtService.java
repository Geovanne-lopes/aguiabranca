package br.com.fiap.aguiabranca.security.jwt;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import br.com.fiap.aguiabranca.common.exception.ApiException;
import br.com.fiap.aguiabranca.security.current.CurrentUser;
import br.com.fiap.aguiabranca.user.domain.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final JwtProperties properties;
    private final Clock clock;
    private final SecretKey key;

    public JwtService(JwtProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
        byte[] secretBytes = requiredSecret(properties.getSecret()).getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(secretBytes);
    }

    public String issueAccessToken(String userId, String email, UserRole role) {
        return issueAccessToken(userId, email, role, properties.getAccessTokenTtl());
    }

    public String issueAccessToken(String userId, String email, UserRole role, Duration ttl) {
        Instant now = clock.instant();
        Instant expiresAt = now.plus(ttl);
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(userId)
                .claim("email", email)
                .claim("role", role.name())
                .issuer(properties.getIssuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(key)
                .compact();
    }

    public CurrentUser parseAccessToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .requireIssuer(properties.getIssuer())
                    .clock(() -> Date.from(clock.instant()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            String roleValue = claims.get("role", String.class);
            String email = claims.get("email", String.class);
            if (claims.getSubject() == null || roleValue == null || email == null) {
                throw ApiException.invalidToken();
            }
            return new CurrentUser(claims.getSubject(), email, UserRole.valueOf(roleValue));
        } catch (ExpiredJwtException ex) {
            throw ApiException.tokenExpired();
        } catch (JwtException | IllegalArgumentException ex) {
            throw ApiException.invalidToken();
        }
    }

    public long expiresInSeconds() {
        return properties.getAccessTokenTtl().toSeconds();
    }

    public Duration refreshTokenTtl() {
        return properties.getRefreshTokenTtl();
    }

    private String requiredSecret(String secret) {
        if (secret == null || secret.isBlank() || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(
                    "JWT_SECRET deve ter no mínimo 32 bytes. Copie backend/.env.example para backend/.env "
                            + "ou defina a variável de ambiente JWT_SECRET."
            );
        }
        return secret;
    }
}
