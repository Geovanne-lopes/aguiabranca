package br.com.fiap.aguiabranca.insight.infra;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class AdviceSlipClient {

    private static final Logger log = LoggerFactory.getLogger(AdviceSlipClient.class);

    private final RestClient restClient;

    @Autowired
    public AdviceSlipClient(RestClient.Builder builder, AdviceProperties properties) {
        Duration timeout = properties.getTimeout() == null ? Duration.ofSeconds(5) : properties.getTimeout();
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(timeout);
        String baseUrl = properties.getBaseUrl() == null ? "https://api.adviceslip.com" : properties.getBaseUrl().trim();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        this.restClient = builder.baseUrl(baseUrl).requestFactory(factory).build();
    }

    AdviceSlipClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public AdviceSlipAdvice fetch() {
        AdviceUnavailableException last = null;
        for (int attempt = 1; attempt <= 2; attempt++) {
            long started = System.nanoTime();
            try {
                AdviceSlipAdvice advice = read();
                log.info("AdviceSlip durationMs={} attempt={} outcome=success", elapsedMs(started), attempt);
                return advice;
            } catch (AdviceUnavailableException ex) {
                log.info(
                        "AdviceSlip durationMs={} attempt={} outcome={}",
                        elapsedMs(started),
                        attempt,
                        ex.isRetryable() ? "retryable" : "failure"
                );
                last = ex;
                if (!ex.isRetryable() || attempt == 2) {
                    throw ex;
                }
            }
        }
        throw last;
    }

    private AdviceSlipAdvice read() {
        try {
            AdviceSlipPayload payload = restClient.get().uri("/advice").retrieve().body(AdviceSlipPayload.class);
            if (payload == null || payload.slip() == null || payload.slip().advice() == null || payload.slip().advice().isBlank()) {
                throw new AdviceUnavailableException(false);
            }
            return new AdviceSlipAdvice(payload.slip().id(), payload.slip().advice().trim());
        } catch (HttpServerErrorException | ResourceAccessException ex) {
            throw new AdviceUnavailableException(true, ex);
        } catch (RestClientException ex) {
            throw new AdviceUnavailableException(false, ex);
        }
    }

    private static long elapsedMs(long startedNanos) {
        return Duration.ofNanos(System.nanoTime() - startedNanos).toMillis();
    }
}
