package br.com.fiap.aguiabranca.common.web;

import java.io.IOException;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.aguiabranca.common.api.ApiError;
import br.com.fiap.aguiabranca.common.api.ApiErrorDetail;
import br.com.fiap.aguiabranca.common.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiErrorWriter {

    private final ObjectMapper objectMapper;
    private final Clock clock;

    public ApiErrorWriter(ObjectMapper objectMapper, Clock clock) {
        this.objectMapper = objectMapper;
        this.clock = clock;
    }

    public ApiError body(HttpServletRequest request, HttpStatus status, String code, String message, List<ApiErrorDetail> details) {
        String traceId = MDC.get(RequestContext.TRACE_ID_MDC);
        if (traceId == null || traceId.isBlank()) {
            traceId = request.getHeader(RequestContext.REQUEST_ID_HEADER);
        }
        if (traceId == null || traceId.isBlank()) {
            traceId = "-";
        }
        String resolvedMessage = message;
        if ("INTERNAL_ERROR".equals(code)) {
            resolvedMessage = "Erro interno. Informe o código " + traceId + ".";
        }
        return new ApiError(
                OffsetDateTime.now(clock),
                status.value(),
                code,
                resolvedMessage,
                request.getRequestURI(),
                traceId,
                details == null ? List.of() : details
        );
    }

    public void write(HttpServletRequest request, HttpServletResponse response, ApiException exception) throws IOException {
        write(request, response, exception.getStatus(), exception.getCode(), exception.getMessage(), exception.getDetails());
    }

    public void write(
            HttpServletRequest request,
            HttpServletResponse response,
            HttpStatus status,
            String code,
            String message,
            List<ApiErrorDetail> details
    ) throws IOException {
        if (response.isCommitted()) {
            return;
        }
        ApiError error = body(request, status, code, message, details);
        response.setStatus(status.value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), error);
    }
}
