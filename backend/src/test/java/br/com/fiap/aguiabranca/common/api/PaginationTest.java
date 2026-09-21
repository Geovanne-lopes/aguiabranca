package br.com.fiap.aguiabranca.common.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import br.com.fiap.aguiabranca.common.exception.ApiException;

class PaginationTest {

    private static final Set<String> ALLOWED = Set.of("name", "createdAt");

    @Test
    void defaultsToPageZeroSize20Desc() {
        PageRequest pageable = Pagination.of(null, null, null, null, ALLOWED, "createdAt");

        assertThat(pageable.getPageNumber()).isZero();
        assertThat(pageable.getPageSize()).isEqualTo(20);
        assertThat(pageable.getSort().getOrderFor("createdAt").getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void sizeAboveMaxIsValidationError() {
        assertThatThrownBy(() -> Pagination.of(0, 101, null, null, ALLOWED, "createdAt"))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> {
                    ApiException api = (ApiException) ex;
                    assertThat(api.getCode()).isEqualTo("VALIDATION_ERROR");
                    assertThat(api.getDetails()).extracting(ApiErrorDetail::field).contains("size");
                });
    }

    @Test
    void unknownSortFieldIsValidationError() {
        assertThatThrownBy(() -> Pagination.of(0, 20, "passwordHash", null, ALLOWED, "createdAt"))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> {
                    ApiException api = (ApiException) ex;
                    assertThat(api.getCode()).isEqualTo("VALIDATION_ERROR");
                    assertThat(api.getDetails()).extracting(ApiErrorDetail::field).contains("sort");
                });
    }
}
