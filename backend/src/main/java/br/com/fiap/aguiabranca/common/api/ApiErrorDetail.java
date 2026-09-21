package br.com.fiap.aguiabranca.common.api;

public record ApiErrorDetail(
        String field,
        String issue
) {
}
