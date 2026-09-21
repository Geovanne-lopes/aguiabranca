package br.com.fiap.aguiabranca.common.api;

import java.util.List;

import org.springframework.data.domain.Page;

public record ApiPage<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static <T> ApiPage<T> of(List<T> content, Page<?> page) {
        return new ApiPage<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
