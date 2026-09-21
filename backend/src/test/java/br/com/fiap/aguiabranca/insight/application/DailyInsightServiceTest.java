package br.com.fiap.aguiabranca.insight.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;

import br.com.fiap.aguiabranca.insight.api.dto.DailyInsightResponse;
import br.com.fiap.aguiabranca.insight.infra.AdviceSlipAdvice;
import br.com.fiap.aguiabranca.insight.infra.AdviceSlipClient;
import br.com.fiap.aguiabranca.insight.infra.AdviceUnavailableException;

@ExtendWith(MockitoExtension.class)
class DailyInsightServiceTest {

    @Mock
    private AdviceSlipClient client;

    @Test
    void unavailableAdviceSlipReturnsFallbackWithoutCachingIt() {
        when(client.fetch()).thenThrow(new AdviceUnavailableException(false));
        DailyInsightService service = new DailyInsightService(client, newCache(new AtomicLong()));

        DailyInsightResponse first = service.daily();
        DailyInsightResponse second = service.daily();

        assertThat(first.id()).isZero();
        assertThat(first.message()).isEqualTo(DailyInsightService.FALLBACK_MESSAGE);
        assertThat(second.id()).isZero();
        verify(client, times(2)).fetch();
    }

    @Test
    void successIsCachedForAboutSixtyMinutes() {
        AtomicLong nanos = new AtomicLong();
        Ticker ticker = nanos::get;
        Cache<String, DailyInsightResponse> cache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(60))
                .ticker(ticker)
                .maximumSize(1)
                .build();
        DailyInsightService service = new DailyInsightService(client, cache);
        when(client.fetch()).thenReturn(new AdviceSlipAdvice(42, "Keep it simple."));

        assertThat(service.daily().message()).isEqualTo("Keep it simple.");

        when(client.fetch()).thenThrow(new AdviceUnavailableException(true));
        nanos.addAndGet(Duration.ofMinutes(59).toNanos());
        assertThat(service.daily().id()).isEqualTo(42);

        nanos.addAndGet(Duration.ofMinutes(2).toNanos());
        DailyInsightResponse expired = service.daily();
        assertThat(expired.id()).isZero();
        assertThat(expired.message()).isEqualTo(DailyInsightService.FALLBACK_MESSAGE);
        verify(client, times(2)).fetch();
    }

    private static Cache<String, DailyInsightResponse> newCache(AtomicLong nanos) {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(60))
                .ticker(nanos::get)
                .maximumSize(1)
                .build();
    }
}
