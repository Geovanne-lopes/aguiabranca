package br.com.fiap.aguiabranca.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import br.com.fiap.aguiabranca.auth.application.EmailMasker;

class EmailMaskerTest {

    @Test
    void masksLocalPartKeepingDomain() {
        assertThat(EmailMasker.mask("operador@innovatecorp.com")).isEqualTo("o***@innovatecorp.com");
    }
}
