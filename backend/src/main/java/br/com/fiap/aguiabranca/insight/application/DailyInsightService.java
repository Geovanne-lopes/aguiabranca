package br.com.fiap.aguiabranca.insight.application;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import br.com.fiap.aguiabranca.insight.api.dto.DailyInsightResponse;
import br.com.fiap.aguiabranca.insight.infra.AdviceProperties;
import br.com.fiap.aguiabranca.insight.infra.AdviceSlipAdvice;
import br.com.fiap.aguiabranca.insight.infra.AdviceSlipClient;

@Service
public class DailyInsightService {

    public static final String FALLBACK_MESSAGE = "Pequenas ideias consistentes constroem grandes resultados.";

    private static final Logger log = LoggerFactory.getLogger(DailyInsightService.class);
    private static final String CACHE_KEY = "daily";

    private final AdviceSlipClient client;
    private final Cache<String, DailyInsightResponse> cache;

    @Autowired
    public DailyInsightService(AdviceSlipClient client, AdviceProperties properties) {
        this(client, cache(properties));
    }

    DailyInsightService(AdviceSlipClient client, Cache<String, DailyInsightResponse> cache) {
        this.client = client;
        this.cache = cache;
    }

    public DailyInsightResponse daily() {
        DailyInsightResponse cached = cache.getIfPresent(CACHE_KEY);
        if (cached != null && cached.message() != null && !cached.message().isBlank()) {
            return cached;
        }
        try {
            AdviceSlipAdvice advice = client.fetch();
            DailyInsightResponse response = new DailyInsightResponse(advice.id(), advice.message());
            cache.put(CACHE_KEY, response);
            return response;
        } catch (Exception ex) {
            log.info("AdviceSlip indisponível; respondendo fallback");
            return fallback();
        }
    }

    public void evict() {
        cache.invalidateAll();
    }

    public static DailyInsightResponse fallback() {
        return new DailyInsightResponse(0, FALLBACK_MESSAGE);
    }

    private static Cache<String, DailyInsightResponse> cache(AdviceProperties properties) {
        Duration ttl = properties.getCacheTtl() == null ? Duration.ofMinutes(60) : properties.getCacheTtl();
        return Caffeine.newBuilder().expireAfterWrite(ttl).maximumSize(1).build();
    }
}
