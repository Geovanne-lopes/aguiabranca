package br.com.fiap.aguiabranca.auth.application;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.fiap.aguiabranca.auth.api.dto.AuthResponse;
import br.com.fiap.aguiabranca.auth.infra.RefreshTokenDocument;
import br.com.fiap.aguiabranca.auth.infra.RefreshTokenRepository;
import br.com.fiap.aguiabranca.common.api.ApiErrorDetail;
import br.com.fiap.aguiabranca.common.exception.ApiException;
import br.com.fiap.aguiabranca.security.current.CurrentUser;
import br.com.fiap.aguiabranca.security.jwt.JwtService;
import br.com.fiap.aguiabranca.user.api.dto.UserResponse;
import br.com.fiap.aguiabranca.user.domain.UserRole;
import br.com.fiap.aguiabranca.user.infra.UserDocument;
import br.com.fiap.aguiabranca.user.infra.UserRepository;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final Clock clock;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            Clock clock
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.clock = clock;
    }

    public AuthResponse register(String name, String email, String password, UserRole role) {
        String normalizedEmail = normalizeEmail(email);
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw ApiException.emailAlreadyExists();
        }
        UserDocument user = new UserDocument();
        user.setId(UUID.randomUUID().toString());
        user.setName(name.trim());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        user.setActive(true);
        userRepository.save(user);
        log.info("register success email={} ipRole={}", EmailMasker.mask(normalizedEmail), role);
        return issueTokens(user);
    }

    public AuthResponse login(String email, String password, String ip) {
        String normalizedEmail = normalizeEmail(email);
        UserDocument user = userRepository.findByEmail(normalizedEmail).orElse(null);
        if (user == null || !passwordEncoder.matches(password, user.getPasswordHash())) {
            log.info("login fail email={} ip={}", EmailMasker.mask(normalizedEmail), ip);
            throw ApiException.invalidCredentials();
        }
        if (!user.isActive()) {
            log.info("login disabled email={} ip={}", EmailMasker.mask(normalizedEmail), ip);
            throw ApiException.accountDisabled();
        }
        log.info("login success email={} ip={}", EmailMasker.mask(normalizedEmail), ip);
        return issueTokens(user);
    }

    public AuthResponse refresh(String refreshToken) {
        RefreshTokenDocument stored = requireActiveRefresh(refreshToken);
        UserDocument user = userRepository.findById(stored.getUserId())
                .orElseThrow(ApiException::invalidRefreshToken);
        if (!user.isActive()) {
            stored.setRevoked(true);
            refreshTokenRepository.save(stored);
            throw ApiException.accountDisabled();
        }
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);
        return issueTokens(user);
    }

    public void logout(CurrentUser currentUser, String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenRepository.findByTokenHash(TokenHasher.sha256(refreshToken.trim()))
                    .filter(token -> token.getUserId().equals(currentUser.id()))
                    .ifPresent(token -> {
                        token.setRevoked(true);
                        refreshTokenRepository.save(token);
                    });
            return;
        }
        List<RefreshTokenDocument> tokens = refreshTokenRepository.findByUserId(currentUser.id());
        tokens.forEach(token -> token.setRevoked(true));
        refreshTokenRepository.saveAll(tokens);
    }

    public void resetPassword(String email, String newPassword) {
        String normalizedEmail = normalizeEmail(email);
        userRepository.findByEmail(normalizedEmail).ifPresent(user -> {
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            List<RefreshTokenDocument> tokens = refreshTokenRepository.findByUserId(user.getId());
            tokens.forEach(token -> token.setRevoked(true));
            refreshTokenRepository.saveAll(tokens);
            log.info("password reset email={}", EmailMasker.mask(normalizedEmail));
        });
    }

    public UserResponse me(CurrentUser currentUser) {
        return UserResponse.from(requireUser(currentUser.id()));
    }

    public UserDocument requireUser(String userId) {
        return userRepository.findById(userId).orElseThrow(ApiException::unauthenticated);
    }

    public void validateProfilePatch(String name, String email, String avatarUrl) {
        if ((name == null || name.isBlank()) && (email == null || email.isBlank()) && avatarUrl == null) {
            throw ApiException.validation(
                    "Informe ao menos um campo.",
                    List.of(new ApiErrorDetail("body", "ao menos um campo é obrigatório"))
            );
        }
    }

    private AuthResponse issueTokens(UserDocument user) {
        String accessToken = jwtService.issueAccessToken(user.getId(), user.getEmail(), user.getRole());
        String refreshToken = UUID.randomUUID().toString();
        Instant now = clock.instant();
        RefreshTokenDocument document = new RefreshTokenDocument();
        document.setId(UUID.randomUUID().toString());
        document.setUserId(user.getId());
        document.setTokenHash(TokenHasher.sha256(refreshToken));
        document.setExpiresAt(now.plus(jwtService.refreshTokenTtl()));
        document.setRevoked(false);
        document.setCreatedAt(now);
        refreshTokenRepository.save(document);
        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtService.expiresInSeconds(),
                UserResponse.from(user)
        );
    }

    private RefreshTokenDocument requireActiveRefresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw ApiException.invalidRefreshToken();
        }
        RefreshTokenDocument stored = refreshTokenRepository.findByTokenHash(TokenHasher.sha256(refreshToken.trim()))
                .orElseThrow(ApiException::invalidRefreshToken);
        Instant now = clock.instant();
        if (stored.isRevoked() || stored.getExpiresAt() == null || !stored.getExpiresAt().isAfter(now)) {
            throw ApiException.invalidRefreshToken();
        }
        return stored;
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
