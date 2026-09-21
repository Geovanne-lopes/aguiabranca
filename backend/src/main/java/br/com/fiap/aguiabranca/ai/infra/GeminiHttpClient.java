package br.com.fiap.aguiabranca.ai.infra;

import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class GeminiHttpClient implements GeminiClient {

    static final String FALLBACK_MODEL = "gemini-3.6-flash";
    private static final List<String> MODEL_CANDIDATES = List.of(
            FALLBACK_MODEL,
            "gemini-3.5-flash-lite"
    );
    static final String SYSTEM_INSTRUCTION = """
            Você é um analista de inovação corporativa da InnovateCorp.
            Comente APENAS os números JSON fornecidos.
            Não invente métricas ausentes.
            Não trate suas frases como auditoria ou garantia financeira.
            Estrutura obrigatória em português (Brasil), no máximo 220 palavras:
            1) Interpretação dos resultados
            2) Tendências ou concentrações
            3) Pontos de atenção
            4) Oportunidades e recomendações práticas para a liderança
            Seja concreto e use os números citados.""";

    private static final Logger log = LoggerFactory.getLogger(GeminiHttpClient.class);
    private static final String API_KEY_HEADER = "x-goog-api-key";

    private final RestClient restClient;
    private final GeminiProperties properties;
    private final ObjectMapper objectMapper;
    private final Runnable retryPause;

    @Autowired
    public GeminiHttpClient(RestClient.Builder builder, GeminiProperties properties, ObjectMapper objectMapper) {
        this(client(builder, properties), properties, objectMapper, GeminiHttpClient::pauseOneSecond);
    }

    public GeminiHttpClient(RestClient restClient, GeminiProperties properties, ObjectMapper objectMapper, Runnable retryPause) {
        this.restClient = restClient;
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.retryPause = retryPause;
    }

    @Override
    public GeminiCompletion complete(String dashboardJson) {
        if (!properties.hasApiKey()) {
            log.warn("Gemini key ausente; chamada não enviada");
            return GeminiCompletion.failed(configuredModel(), null);
        }
        GeminiCompletion last = GeminiCompletion.failed(configuredModel(), null);
        for (String model : modelsToTry()) {
            last = call(model, dashboardJson, true);
            log.warn(
                    "Gemini attempt model={} kind={} httpStatus={}",
                    model,
                    last.kind(),
                    last.httpStatus()
            );
            if (last.kind() == GeminiCompletion.Kind.SUCCESS) {
                return last;
            }
            if (last.kind() == GeminiCompletion.Kind.TIMEOUT) {
                return last;
            }
            if (last.httpStatus() != null && last.httpStatus() == 429) {
                return last;
            }
        }
        return last;
    }

    private List<String> modelsToTry() {
        LinkedHashSet<String> models = new LinkedHashSet<>();
        models.add(configuredModel());
        models.addAll(MODEL_CANDIDATES);
        return List.copyOf(models);
    }

    private GeminiCompletion call(String model, String dashboardJson, boolean allowRetry) {
        try {
            JsonNode response = post(model, dashboardJson);
            String text = readText(response);
            if (text == null || text.isBlank()) {
                log.warn("Gemini status=empty-body model={} {}", model, describeEmpty(response));
                return GeminiCompletion.empty(model);
            }
            return GeminiCompletion.success(text, model);
        } catch (HttpClientErrorException.TooManyRequests ex) {
            log.warn("Gemini status=429 model={}", model);
            if (allowRetry) {
                retryPause.run();
                return call(model, dashboardJson, false);
            }
            return GeminiCompletion.failed(model, 429);
        } catch (HttpClientErrorException ex) {
            int status = ex.getStatusCode().value();
            log.warn("Gemini status={} model={} body={}", status, model, sanitizeBody(ex.getResponseBodyAsString()));
            if (status == 404) {
                return GeminiCompletion.modelNotFound(model);
            }
            return GeminiCompletion.failed(model, status);
        } catch (HttpServerErrorException ex) {
            int status = ex.getStatusCode().value();
            log.warn("Gemini status={} model={}", status, model);
            if (allowRetry) {
                retryPause.run();
                return call(model, dashboardJson, false);
            }
            return GeminiCompletion.failed(model, status);
        } catch (ResourceAccessException ex) {
            log.warn("Gemini status=timeout model={}", model);
            return GeminiCompletion.timeout(model);
        } catch (RestClientException ex) {
            log.warn(
                    "Gemini status=error model={} type={} message={}",
                    model,
                    ex.getClass().getSimpleName(),
                    sanitizeBody(String.valueOf(ex.getMessage()))
            );
            return GeminiCompletion.failed(model, null);
        }
    }

    private JsonNode post(String model, String dashboardJson) {
        ObjectNode body = objectMapper.createObjectNode();
        body.set("systemInstruction", objectMapper.valueToTree(Map.of(
                "parts", List.of(Map.of("text", SYSTEM_INSTRUCTION))
        )));
        body.set("contents", objectMapper.valueToTree(List.of(Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", "Dados do dashboard:\n" + dashboardJson))
        ))));
        ObjectNode generation = body.putObject("generationConfig");
        generation.put("temperature", 0.4);
        generation.put("maxOutputTokens", 2048);

        return restClient.post()
                .uri("/v1beta/models/" + model + ":generateContent")
                .header(API_KEY_HEADER, properties.getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(JsonNode.class);
    }

    private static String readText(JsonNode response) {
        if (response == null) {
            return "";
        }
        JsonNode candidates = response.get("candidates");
        if (candidates == null || !candidates.isArray() || candidates.isEmpty()) {
            return "";
        }
        StringBuilder text = new StringBuilder();
        JsonNode parts = candidates.get(0).path("content").path("parts");
        if (!parts.isArray()) {
            return "";
        }
        for (JsonNode part : parts) {
            JsonNode value = part.get("text");
            if (value != null && value.isTextual()) {
                text.append(value.asText());
            }
        }
        return text.toString();
    }

    private static String describeEmpty(JsonNode response) {
        if (response == null) {
            return "null-response";
        }
        JsonNode first = response.path("candidates").path(0);
        return "finishReason=" + first.path("finishReason").asText("")
                + " blockReason=" + response.path("promptFeedback").path("blockReason").asText("")
                + " candidates=" + response.path("candidates").size();
    }

    private static String sanitizeBody(String body) {
        if (body == null || body.isBlank()) {
            return "";
        }
        String cleaned = body.replaceAll("(?i)(AIza[\\w-]+|AQ\\.[\\w-]+)", "[redacted]");
        return cleaned.length() > 240 ? cleaned.substring(0, 240) : cleaned;
    }

    private String configuredModel() {
        String model = properties.getModel();
        if (model == null || model.isBlank()) {
            return FALLBACK_MODEL;
        }
        return model.trim();
    }

    private static RestClient client(RestClient.Builder builder, GeminiProperties properties) {
        Duration timeout = properties.getTimeout() == null ? Duration.ofSeconds(12) : properties.getTimeout();
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(timeout);
        String baseUrl = properties.getBaseUrl() == null
                ? "https://generativelanguage.googleapis.com"
                : properties.getBaseUrl().trim();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return builder.baseUrl(baseUrl).requestFactory(factory).build();
    }

    private static void pauseOneSecond() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
