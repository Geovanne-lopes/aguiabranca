package br.com.fiap.aguiabranca.common.exception;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import br.com.fiap.aguiabranca.common.api.ApiError;
import br.com.fiap.aguiabranca.common.api.ApiErrorDetail;
import br.com.fiap.aguiabranca.common.web.ApiErrorWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ApiErrorWriter errorWriter;

    public GlobalExceptionHandler(ApiErrorWriter errorWriter) {
        this.errorWriter = errorWriter;
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApi(ApiException ex, HttpServletRequest request) {
        log.warn("API error code={} path={} message={}", ex.getCode(), request.getRequestURI(), ex.getMessage());
        return respond(request, ex.getStatus(), ex.getCode(), ex.getMessage(), ex.getDetails());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ApiErrorDetail> details = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toDetail)
                .toList();
        log.warn("Validation error path={} fields={}", request.getRequestURI(), details.size());
        return respond(request, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Um ou mais campos são inválidos.", details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraint(ConstraintViolationException ex, HttpServletRequest request) {
        List<ApiErrorDetail> details = ex.getConstraintViolations().stream()
                .map(this::toDetail)
                .toList();
        log.warn("Constraint violation path={}", request.getRequestURI());
        return respond(request, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Um ou mais campos são inválidos.", details);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleMalformed(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Malformed JSON path={}", request.getRequestURI());
        return respond(request, HttpStatus.BAD_REQUEST, "MALFORMED_JSON", "JSON malformado.", List.of());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        List<ApiErrorDetail> details = List.of(new ApiErrorDetail(ex.getName(), "tipo inválido"));
        return respond(request, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Um ou mais campos são inválidos.", details);
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<ApiError> handleNotFound(Exception ex, HttpServletRequest request) {
        return respond(request, HttpStatus.NOT_FOUND, "NOT_FOUND", "Recurso não encontrado.", List.of());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ApiError> handleDuplicate(DuplicateKeyException ex, HttpServletRequest request) {
        log.warn("Duplicate key path={}", request.getRequestURI());
        return respond(request, HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS", "E-mail já cadastrado.", List.of());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleDenied(AccessDeniedException ex, HttpServletRequest request) {
        return respond(request, HttpStatus.FORBIDDEN, "FORBIDDEN", "Perfil não autorizado para este recurso.", List.of());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        return respond(request, HttpStatus.UNAUTHORIZED, "UNAUTHENTICATED", "Autenticação necessária.", List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unhandled error path={}", request.getRequestURI(), ex);
        ApiError body = errorWriter.body(request, HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "", List.of());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private ResponseEntity<ApiError> respond(
            HttpServletRequest request,
            HttpStatus status,
            String code,
            String message,
            List<ApiErrorDetail> details
    ) {
        return ResponseEntity.status(status).body(errorWriter.body(request, status, code, message, details));
    }

    private ApiErrorDetail toDetail(FieldError error) {
        return new ApiErrorDetail(error.getField(), error.getDefaultMessage());
    }

    private ApiErrorDetail toDetail(ConstraintViolation<?> violation) {
        String field = violation.getPropertyPath() == null ? "" : violation.getPropertyPath().toString();
        return new ApiErrorDetail(field, violation.getMessage());
    }
}
