package br.com.fiap.aguiabranca.common.exception;

import java.util.List;

import org.springframework.http.HttpStatus;

import br.com.fiap.aguiabranca.common.api.ApiErrorDetail;

public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String code;
    private final List<ApiErrorDetail> details;

    public ApiException(HttpStatus status, String code, String message) {
        this(status, code, message, List.of());
    }

    public ApiException(HttpStatus status, String code, String message, List<ApiErrorDetail> details) {
        super(message);
        this.status = status;
        this.code = code;
        this.details = details == null ? List.of() : List.copyOf(details);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public List<ApiErrorDetail> getDetails() {
        return details;
    }

    public static ApiException invalidCredentials() {
        return new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "E-mail ou senha inválidos.");
    }

    public static ApiException accountDisabled() {
        return new ApiException(HttpStatus.UNAUTHORIZED, "ACCOUNT_DISABLED", "Conta desativada.");
    }

    public static ApiException unauthenticated() {
        return new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHENTICATED", "Autenticação necessária.");
    }

    public static ApiException invalidToken() {
        return new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "Token inválido.");
    }

    public static ApiException tokenExpired() {
        return new ApiException(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", "Token expirado.");
    }

    public static ApiException invalidRefreshToken() {
        return new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_REFRESH_TOKEN", "Refresh token inválido ou revogado.");
    }

    public static ApiException forbidden() {
        return new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "Perfil não autorizado para este recurso.");
    }

    public static ApiException notFound() {
        return new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Recurso não encontrado.");
    }

    public static ApiException emailAlreadyExists() {
        return new ApiException(HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS", "E-mail já cadastrado.");
    }

    public static ApiException invalidStatusTransition() {
        return new ApiException(HttpStatus.CONFLICT, "INVALID_STATUS_TRANSITION", "Transição de status não permitida.");
    }

    public static ApiException ideaAlreadyHasProject() {
        return new ApiException(HttpStatus.CONFLICT, "IDEA_ALREADY_HAS_PROJECT", "Não é possível reprovar uma ideia que já possui projeto.");
    }

    public static ApiException ideaAlreadyLinkedToProject() {
        return new ApiException(HttpStatus.CONFLICT, "IDEA_ALREADY_HAS_PROJECT", "A ideia já possui um projeto.");
    }

    public static ApiException rateLimited() {
        return new ApiException(HttpStatus.TOO_MANY_REQUESTS, "RATE_LIMITED", "Muitas tentativas. Tente novamente mais tarde.");
    }

    public static ApiException validation(String message, List<ApiErrorDetail> details) {
        return new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message, details);
    }

    public static ApiException malformedJson() {
        return new ApiException(HttpStatus.BAD_REQUEST, "MALFORMED_JSON", "JSON malformado.");
    }
}
