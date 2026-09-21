package br.com.fiap.aguiabranca.auth.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.aguiabranca.auth.api.dto.AuthResponse;
import br.com.fiap.aguiabranca.auth.api.dto.LoginRequest;
import br.com.fiap.aguiabranca.auth.api.dto.LogoutRequest;
import br.com.fiap.aguiabranca.auth.api.dto.RefreshRequest;
import br.com.fiap.aguiabranca.auth.api.dto.RegisterRequest;
import br.com.fiap.aguiabranca.auth.api.dto.ResetPasswordRequest;
import br.com.fiap.aguiabranca.auth.application.AuthService;
import br.com.fiap.aguiabranca.security.current.CurrentUserAccessor;
import br.com.fiap.aguiabranca.security.ratelimit.RateLimitService;
import br.com.fiap.aguiabranca.user.api.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth")
public class AuthController {

    private final AuthService authService;
    private final RateLimitService rateLimitService;
    private final CurrentUserAccessor currentUserAccessor;

    public AuthController(
            AuthService authService,
            RateLimitService rateLimitService,
            CurrentUserAccessor currentUserAccessor
    ) {
        this.authService = authService;
        this.rateLimitService = rateLimitService;
        this.currentUserAccessor = currentUserAccessor;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuário")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest
    ) {
        rateLimitService.checkRegister(clientIp(httpRequest));
        AuthResponse response = authService.register(
                request.name(),
                request.email(),
                request.password(),
                request.role()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar e emitir JWT")
    public AuthResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ip = clientIp(httpRequest);
        rateLimitService.checkLogin(ip, request.email());
        return authService.login(request.email(), request.password(), ip);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar o par de tokens")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request.refreshToken());
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Encerrar sessão e revogar refresh")
    public ResponseEntity<Void> logout(@RequestBody(required = false) LogoutRequest request) {
        String refreshToken = request == null ? null : request.refreshToken();
        authService.logout(currentUserAccessor.require(), refreshToken);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Redefinir senha (fluxo acadêmico, sem e-mail)")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request,
            HttpServletRequest httpRequest
    ) {
        rateLimitService.checkResetPassword(clientIp(httpRequest));
        authService.resetPassword(request.email(), request.newPassword());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Usuário autenticado")
    public UserResponse me() {
        return authService.me(currentUserAccessor.require());
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr() == null ? "unknown" : request.getRemoteAddr();
    }
}
