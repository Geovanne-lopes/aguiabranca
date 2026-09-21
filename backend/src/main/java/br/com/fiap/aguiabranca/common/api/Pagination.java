package br.com.fiap.aguiabranca.common.api;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import br.com.fiap.aguiabranca.common.exception.ApiException;

public final class Pagination {

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;

    private Pagination() {
    }

    public static PageRequest of(
            Integer page,
            Integer size,
            String sort,
            String direction,
            Set<String> allowedSortFields,
            String defaultSort
    ) {
        int resolvedPage = page == null ? DEFAULT_PAGE : page;
        if (resolvedPage < 0) {
            throw ApiException.validation(
                    "Um ou mais campos são inválidos.",
                    List.of(new ApiErrorDetail("page", "deve ser no mínimo 0"))
            );
        }

        int resolvedSize = size == null ? DEFAULT_SIZE : size;
        if (resolvedSize < 1) {
            throw ApiException.validation(
                    "Um ou mais campos são inválidos.",
                    List.of(new ApiErrorDetail("size", "deve ser no mínimo 1"))
            );
        }
        if (resolvedSize > MAX_SIZE) {
            throw ApiException.validation(
                    "Um ou mais campos são inválidos.",
                    List.of(new ApiErrorDetail("size", "deve ser no máximo 100"))
            );
        }

        String resolvedSort = (sort == null || sort.isBlank()) ? defaultSort : sort.trim();
        if (!allowedSortFields.contains(resolvedSort)) {
            throw ApiException.validation(
                    "Um ou mais campos são inválidos.",
                    List.of(new ApiErrorDetail("sort", "campo de ordenação não permitido"))
            );
        }

        return PageRequest.of(resolvedPage, resolvedSize, Sort.by(parseDirection(direction), resolvedSort));
    }

    private static Sort.Direction parseDirection(String direction) {
        if (direction == null || direction.isBlank()) {
            return Sort.Direction.DESC;
        }
        try {
            return Sort.Direction.fromString(direction.trim());
        } catch (IllegalArgumentException ex) {
            throw ApiException.validation(
                    "Um ou mais campos são inválidos.",
                    List.of(new ApiErrorDetail("direction", "deve ser ASC ou DESC"))
            );
        }
    }
}
