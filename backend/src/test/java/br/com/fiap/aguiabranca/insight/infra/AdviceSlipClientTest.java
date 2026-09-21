package br.com.fiap.aguiabranca.insight.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class AdviceSlipClientTest {

    private MockRestServiceServer server;
    private AdviceSlipClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://advice.test");
        server = MockRestServiceServer.bindTo(builder).build();
        client = new AdviceSlipClient(builder.build());
    }

    @Test
    void mapsSlipToIdAndMessage() {
        server.expect(requestTo("http://advice.test/advice"))
                .andRespond(withSuccess("{\"slip\":{\"id\":123,\"advice\":\"Keep it simple.\"}}", MediaType.APPLICATION_JSON));

        AdviceSlipAdvice advice = client.fetch();

        assertThat(advice.id()).isEqualTo(123);
        assertThat(advice.message()).isEqualTo("Keep it simple.");
        server.verify();
    }

    @Test
    void retriesOnceOnServerError() {
        server.expect(requestTo("http://advice.test/advice"))
                .andRespond(withStatus(HttpStatus.BAD_GATEWAY));
        server.expect(requestTo("http://advice.test/advice"))
                .andRespond(withSuccess("{\"slip\":{\"id\":7,\"advice\":\"Try again.\"}}", MediaType.APPLICATION_JSON));

        AdviceSlipAdvice advice = client.fetch();

        assertThat(advice.id()).isEqualTo(7);
        assertThat(advice.message()).isEqualTo("Try again.");
        server.verify();
    }

    @Test
    void retriesOnceOnIoFailure() {
        server.expect(requestTo("http://advice.test/advice"))
                .andRespond(withException(new SocketTimeoutException("timed out")));
        server.expect(requestTo("http://advice.test/advice"))
                .andRespond(withSuccess("{\"slip\":{\"id\":8,\"advice\":\"After timeout.\"}}", MediaType.APPLICATION_JSON));

        AdviceSlipAdvice advice = client.fetch();

        assertThat(advice.message()).isEqualTo("After timeout.");
        server.verify();
    }

    @Test
    void doesNotRetryClientError() {
        server.expect(requestTo("http://advice.test/advice"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatThrownBy(client::fetch)
                .isInstanceOf(AdviceUnavailableException.class)
                .matches(ex -> !((AdviceUnavailableException) ex).isRetryable());
        server.verify();
    }

    @Test
    void givesUpAfterOneRetry() {
        server.expect(requestTo("http://advice.test/advice"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        server.expect(requestTo("http://advice.test/advice"))
                .andRespond(withException(new IOException("reset")));

        assertThatThrownBy(client::fetch).isInstanceOf(AdviceUnavailableException.class);
        server.verify();
    }
}
