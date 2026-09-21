package br.com.fiap.aguiabranca.seed;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.fiap.aguiabranca.AbstractMongoIT;
import br.com.fiap.aguiabranca.guideline.infra.GuidelineRepository;
import br.com.fiap.aguiabranca.user.infra.UserRepository;

class SeedIdempotencyIT extends AbstractMongoIT {

    @Autowired
    private DataInitializer dataInitializer;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GuidelineRepository guidelineRepository;

    @Test
    @DisplayName("A-OPS-01 seed local cria as 3 contas demo e restart não duplica")
    void aOps01_seedIsIdempotent() {
        dataInitializer.seed();
        dataInitializer.seed();

        assertThat(userRepository.findByEmail("operador@innovatecorp.com")).isPresent();
        assertThat(userRepository.findByEmail("gestor@innovatecorp.com")).isPresent();
        assertThat(userRepository.findByEmail("lideranca@innovatecorp.com")).isPresent();
        assertThat(userRepository.findAll().stream()
                .filter(user -> user.getEmail().equals("operador@innovatecorp.com"))
                .count()).isEqualTo(1);
        assertThat(userRepository.findAll().stream()
                .filter(user -> user.getEmail().equals("gestor@innovatecorp.com"))
                .count()).isEqualTo(1);
        assertThat(userRepository.findAll().stream()
                .filter(user -> user.getEmail().equals("lideranca@innovatecorp.com"))
                .count()).isEqualTo(1);
        assertThat(guidelineRepository.findByTitle("Transformação Digital")).isPresent();
        assertThat(guidelineRepository.findByTitle("Sustentabilidade Corporativa")).isPresent();
        assertThat(guidelineRepository.findAll().stream()
                .filter(guideline -> guideline.getTitle().equals("Transformação Digital"))
                .count()).isEqualTo(1);
        assertThat(guidelineRepository.findAll().stream()
                .filter(guideline -> guideline.getTitle().equals("Sustentabilidade Corporativa"))
                .count()).isEqualTo(1);
    }
}
