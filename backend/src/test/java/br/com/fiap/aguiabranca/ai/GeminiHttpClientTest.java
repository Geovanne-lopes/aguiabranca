package br.com.fiap.aguiabranca.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.net.SocketTimeoutException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.aguiabranca.ai.infra.GeminiCompletion;
import br.com.fiap.aguiabranca.ai.infra.GeminiHttpClient;
import br.com.fiap.aguiabranca.ai.infra.GeminiProperties;

class GeminiHttpClientTest {

    private static final String URL = "http://gemini.test/v1beta/models/gemini-2.0-flash:generateContent";

    private MockRestServiceServer server;
    private GeminiProperties properties;
    private GeminiHttpClient client;

    @BeforeEach
    void setUp() {
        properties = new GeminiProperties();
        properties.setApiKey("test-gemini-key");
        properties.setModel("gemini-2.0-flash");
        properties.setBaseUrl("http://gemini.test");
        RestClient.Builder builder = RestClient.builder().baseUrl("http://gemini.test");
        server = MockRestServiceServer.bindTo(builder).build();
        client = new GeminiHttpClient(builder.build(), properties, new ObjectMapper(), () -> {
        });
    }

    @Test
    void successReadsTextAndSendsKeyOnlyInHeader() {
        server.expect(requestTo(URL))
                .andExpect(header("x-goog-api-key", "test-gemini-key"))
                .andRespond(withSuccess(
                        "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"O ROI consolidado indica concentração.\"}]}}]}",
                        MediaType.APPLICATION_JSON
                ));

        GeminiCompletion completion = client.complete("{\"totalProjects\":1}");

        assertThat(completion.kind()).isEqualTo(GeminiCompletion.Kind.SUCCESS);
        assertThat(completion.text()).isEqualTo("O ROI consolidado indica concentração.");
        server.verify();
    }

    @Test
    void timeoutDoesNotRetry() {
        server.expect(requestTo(URL))
                .andRespond(withException(new SocketTimeoutException("timed out")));

        GeminiCompletion completion = client.complete("{}");

        assertThat(completion.kind()).isEqualTo(GeminiCompletion.Kind.TIMEOUT);
        server.verify();
    }

    @Test
    void emptyCandidatesAreEmpty() {
        for (String model : new String[] {
                "gemini-2.0-flash",
                "gemini-3.6-flash",
                "gemini-3.5-flash-lite"
        }) {
            server.expect(requestTo("http://gemini.test/v1beta/models/" + model + ":generateContent"))
                    .andRespond(withSuccess("{\"candidates\":[]}", MediaType.APPLICATION_JSON));
        }

        GeminiCompletion completion = client.complete("{}");

        assertThat(completion.kind()).isEqualTo(GeminiCompletion.Kind.EMPTY);
        server.verify();
    }

    @Test
    void notFoundTriesNextModel() {
        server.expect(requestTo(URL)).andRespond(withStatus(HttpStatus.NOT_FOUND));
        server.expect(requestTo("http://gemini.test/v1beta/models/gemini-3.6-flash:generateContent"))
                .andRespond(withSuccess(
                        "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"modelo seguinte\"}]}}]}",
                        MediaType.APPLICATION_JSON
                ));

        GeminiCompletion completion = client.complete("{}");

        assertThat(completion.kind()).isEqualTo(GeminiCompletion.Kind.SUCCESS);
        assertThat(completion.text()).isEqualTo("modelo seguinte");
        assertThat(completion.model()).isEqualTo("gemini-3.6-flash");
        server.verify();
    }

    @Test
    void retriesOnceOn429() {
        server.expect(requestTo(URL)).andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));
        server.expect(requestTo(URL))
                .andRespond(withSuccess(
                        "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"depois do retry\"}]}}]}",
                        MediaType.APPLICATION_JSON
                ));

        GeminiCompletion completion = client.complete("{}");

        assertThat(completion.kind()).isEqualTo(GeminiCompletion.Kind.SUCCESS);
        assertThat(completion.text()).isEqualTo("depois do retry");
        server.verify();
    }

    @Test
    void blankKeyDoesNotCallNetwork() {
        properties.setApiKey("  ");

        GeminiCompletion completion = client.complete("{}");

        assertThat(completion.kind()).isEqualTo(GeminiCompletion.Kind.FAILED);
        server.verify();
    }
}
