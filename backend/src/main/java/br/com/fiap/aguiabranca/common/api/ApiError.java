package br.com.fiap.aguiabranca.common.api;

import java.time.OffsetDateTime;
import java.util.List;

public record ApiError(
        OffsetDateTime timestamp,
        int status,
        String code,
        String message,
        String path,
        String traceId,
        List<ApiErrorDetail> details
) {
}
