package br.com.fiap.aguiabranca.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.fiap.aguiabranca.common.exception.ApiException;
import br.com.fiap.aguiabranca.security.current.CurrentUser;
import br.com.fiap.aguiabranca.security.jwt.JwtProperties;
import br.com.fiap.aguiabranca.security.jwt.JwtService;
import br.com.fiap.aguiabranca.user.domain.UserRole;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("test-jwt-secret-must-be-32-bytes-min");
        properties.setIssuer("aguiabranca");
        properties.setAccessTokenTtl(Duration.ofHours(1));
        properties.setRefreshTokenTtl(Duration.ofDays(7));
        jwtService = new JwtService(properties, Clock.fixed(Instant.parse("2026-09-21T12:00:00Z"), ZoneOffset.UTC));
    }

    @Test
    void issuesAndParsesAccessTokenWithRoleClaim() {
        String token = jwtService.issueAccessToken("user-1", "operador@innovatecorp.com", UserRole.OPERATOR);
        CurrentUser user = jwtService.parseAccessToken(token);
        assertThat(user.id()).isEqualTo("user-1");
        assertThat(user.email()).isEqualTo("operador@innovatecorp.com");
        assertThat(user.role()).isEqualTo(UserRole.OPERATOR);
        assertThat(jwtService.expiresInSeconds()).isEqualTo(3600);
    }

    @Test
    void expiredTokenRaisesTokenExpired() {
        String token = jwtService.issueAccessToken(
                "user-1",
                "operador@innovatecorp.com",
                UserRole.OPERATOR,
                Duration.ofMinutes(-5)
        );
        assertThatThrownBy(() -> jwtService.parseAccessToken(token))
                .isInstanceOf(ApiException.class)
                .extracting(ex -> ((ApiException) ex).getCode())
                .isEqualTo("TOKEN_EXPIRED");
    }
}
