package br.com.fiap.aguiabranca.seed;

import java.time.Instant;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.fiap.aguiabranca.guideline.domain.GuidelineHistoryAction;
import br.com.fiap.aguiabranca.guideline.infra.GuidelineDocument;
import br.com.fiap.aguiabranca.guideline.infra.GuidelineHistoryDocument;
import br.com.fiap.aguiabranca.guideline.infra.GuidelineHistoryRepository;
import br.com.fiap.aguiabranca.guideline.infra.GuidelineRepository;
import br.com.fiap.aguiabranca.user.domain.UserRole;
import br.com.fiap.aguiabranca.user.infra.UserDocument;
import br.com.fiap.aguiabranca.user.infra.UserRepository;

@Component
@Order(2)
public class DataInitializer implements ApplicationRunner {

    public static final String OPERATOR_ID = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";
    public static final String MANAGER_ID = "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb";
    public static final String LEADER_ID = "cccccccc-cccc-cccc-cccc-cccccccccccc";

    public static final String GUIDELINE_DIGITAL_ID = "dddddddd-dddd-dddd-dddd-dddddddddddd";
    public static final String GUIDELINE_ESG_ID = "eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee";

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final GuidelineRepository guidelineRepository;
    private final GuidelineHistoryRepository historyRepository;
    private final PasswordEncoder passwordEncoder;
    private final SeedProperties seedProperties;

    public DataInitializer(
            UserRepository userRepository,
            GuidelineRepository guidelineRepository,
            GuidelineHistoryRepository historyRepository,
            PasswordEncoder passwordEncoder,
            SeedProperties seedProperties
    ) {
        this.userRepository = userRepository;
        this.guidelineRepository = guidelineRepository;
        this.historyRepository = historyRepository;
        this.passwordEncoder = passwordEncoder;
        this.seedProperties = seedProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!seedProperties.enabled()) {
            return;
        }
        seed();
    }

    public void seed() {
        seedUser(OPERATOR_ID, "Ana Operadora", "operador@innovatecorp.com", "oper123", UserRole.OPERATOR);
        seedUser(MANAGER_ID, "Carlos Gestor", "gestor@innovatecorp.com", "gest123", UserRole.MANAGER);
        seedUser(LEADER_ID, "Mariana Costa", "lideranca@innovatecorp.com", "lider123", UserRole.LEADER);
        seedGuideline(
                GUIDELINE_DIGITAL_ID,
                "Transformação Digital",
                "Priorizar iniciativas que digitalizem processos operacionais e reduzam retrabalho."
        );
        seedGuideline(
                GUIDELINE_ESG_ID,
                "Sustentabilidade Corporativa",
                "Incentivar ideias com impacto ambiental mensurável e metas ESG claras."
        );
        log.info("Seed de usuários e diretrizes demo concluído (idempotente).");
    }

    private void seedUser(String id, String name, String email, String rawPassword, UserRole role) {
        if (userRepository.existsByEmail(email)) {
            return;
        }
        UserDocument user = new UserDocument();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setActive(true);
        user.setAvatarUrl(null);
        try {
            userRepository.save(user);
        } catch (DuplicateKeyException ignored) {
            // corrida com outro boot/teste: o índice unique garante idempotência
        }
    }

    private void seedGuideline(String id, String title, String content) {
        if (!guidelineRepository.existsById(id) && !guidelineRepository.existsByTitle(title)) {
            GuidelineDocument guideline = new GuidelineDocument();
            guideline.setId(id);
            guideline.setTitle(title);
            guideline.setContent(content);
            guideline.setCategory(null);
            guideline.setCampaign(null);
            guideline.setVersion(1);
            guideline.setAuthorId(LEADER_ID);
            try {
                if (!historyRepository.existsByGuidelineIdAndAction(id, GuidelineHistoryAction.CREATED)) {
                    historyRepository.save(createdHistory(guideline));
                }
                guidelineRepository.save(guideline);
            } catch (DuplicateKeyException ignored) {
                // corrida com outro boot/teste
            }
            return;
        }
        if (!historyRepository.existsByGuidelineIdAndAction(id, GuidelineHistoryAction.CREATED)) {
            GuidelineDocument existing = guidelineRepository.findById(id)
                    .or(() -> guidelineRepository.findByTitle(title))
                    .orElse(null);
            if (existing != null) {
                try {
                    historyRepository.save(createdHistory(existing));
                } catch (DuplicateKeyException ignored) {
                    // já existe evento CREATED
                }
            }
        }
    }

    private GuidelineHistoryDocument createdHistory(GuidelineDocument guideline) {
        GuidelineHistoryDocument history = new GuidelineHistoryDocument();
        history.setId(UUID.randomUUID().toString());
        history.setGuidelineId(guideline.getId());
        history.setVersion(guideline.getVersion());
        history.setAction(GuidelineHistoryAction.CREATED);
        history.setTitle(guideline.getTitle());
        history.setContent(guideline.getContent());
        history.setCategory(guideline.getCategory());
        history.setCampaign(guideline.getCampaign());
        history.setActorUserId(LEADER_ID);
        history.setOccurredAt(Instant.now());
        return history;
    }
}
