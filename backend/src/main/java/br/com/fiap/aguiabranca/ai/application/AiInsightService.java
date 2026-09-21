package br.com.fiap.aguiabranca.ai.application;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.fiap.aguiabranca.ai.api.dto.AiInsightResponse;
import br.com.fiap.aguiabranca.ai.api.dto.GenerateInsightRequest;
import br.com.fiap.aguiabranca.ai.api.dto.InsightBasisResponse;
import br.com.fiap.aguiabranca.ai.domain.InsightRecordStatus;
import br.com.fiap.aguiabranca.ai.domain.InsightText;
import br.com.fiap.aguiabranca.ai.infra.AiInsightDocument;
import br.com.fiap.aguiabranca.ai.infra.AiInsightRepository;
import br.com.fiap.aguiabranca.ai.infra.GeminiClient;
import br.com.fiap.aguiabranca.ai.infra.GeminiCompletion;
import br.com.fiap.aguiabranca.ai.infra.GeminiProperties;
import br.com.fiap.aguiabranca.auth.application.TokenHasher;
import br.com.fiap.aguiabranca.common.exception.NotFoundException;
import br.com.fiap.aguiabranca.dashboard.application.DashboardService;
import br.com.fiap.aguiabranca.dashboard.application.InsightDataset;
import br.com.fiap.aguiabranca.dashboard.domain.PortfolioSummary;
import br.com.fiap.aguiabranca.security.current.CurrentUser;
import br.com.fiap.aguiabranca.security.ratelimit.RateLimitService;

@Service
public class AiInsightService {

    private static final Logger log = LoggerFactory.getLogger(AiInsightService.class);
    private static final int MAX_PER_USER = 20;
    private static final List<InsightRecordStatus> VISIBLE = List.of(
            InsightRecordStatus.SUCCESS,
            InsightRecordStatus.FALLBACK
    );

    private final DashboardService dashboardService;
    private final GeminiClient geminiClient;
    private final GeminiProperties properties;
    private final AiInsightRepository repository;
    private final RateLimitService rateLimitService;
    private final Clock clock;

    public AiInsightService(
            DashboardService dashboardService,
            GeminiClient geminiClient,
            GeminiProperties properties,
            AiInsightRepository repository,
            RateLimitService rateLimitService,
            Clock clock
    ) {
        this.dashboardService = dashboardService;
        this.geminiClient = geminiClient;
        this.properties = properties;
        this.repository = repository;
        this.rateLimitService = rateLimitService;
        this.clock = clock;
    }

    public AiInsightResponse generate(CurrentUser user, GenerateInsightRequest request) {
        rateLimitService.checkAiGeneration(user.id());
        Instant from = request == null ? null : request.from();
        Instant to = request == null ? null : request.to();
        InsightDataset dataset = dashboardService.insightDataset(from, to);
        String json = GeminiDashboardPayload.json(dataset);
        long started = System.nanoTime();
        PortfolioSummary portfolio = dataset.portfolio();
        if (!properties.hasApiKey()) {
            log.warn("Gemini key ausente; usando fallback userId={}", user.id());
            return persist(
                    user,
                    dataset,
                    json,
                    InsightText.fallback(portfolio.overallRoiPercent(), portfolio.totalInvestment(), portfolio.totalProjects()),
                    InsightRecordStatus.FALLBACK,
                    configuredModel(),
                    started
            );
        }
        GeminiCompletion completion = ask(json);
        String content = completion.kind() == GeminiCompletion.Kind.SUCCESS
                ? InsightText.normalize(completion.text())
                : null;
        if (content == null) {
            log.warn(
                    "ai.generate fallback kind={} httpStatus={} model={}",
                    completion.kind(),
                    completion.httpStatus(),
                    completion.model()
            );
            return persist(
                    user,
                    dataset,
                    json,
                    InsightText.fallback(portfolio.overallRoiPercent(), portfolio.totalInvestment(), portfolio.totalProjects()),
                    InsightRecordStatus.FALLBACK,
                    completion.model() == null ? configuredModel() : completion.model(),
                    started
            );
        }
        return persist(user, dataset, json, content, InsightRecordStatus.SUCCESS, completion.model(), started);
    }

    public AiInsightResponse latest(CurrentUser user) {
        List<AiInsightDocument> found = repository.findByRequestedByUserIdAndStatusInOrderByCreatedAtDesc(
                user.id(),
                VISIBLE
        );
        if (found.isEmpty()) {
            throw new NotFoundException();
        }
        return toResponse(found.get(0));
    }

    private GeminiCompletion ask(String json) {
        try {
            return geminiClient.complete(json);
        } catch (RuntimeException ex) {
            log.warn("Gemini status=error model={}", configuredModel());
            return GeminiCompletion.failed(configuredModel(), null);
        }
    }

    private AiInsightResponse persist(
            CurrentUser user,
            InsightDataset dataset,
            String json,
            String content,
            InsightRecordStatus status,
            String model,
            long startedNanos
    ) {
        PortfolioSummary portfolio = dataset.portfolio();
        AiInsightDocument document = new AiInsightDocument();
        document.setId(UUID.randomUUID().toString());
        document.setRequestedByUserId(user.id());
        document.setModel(model);
        document.setPromptHash(TokenHasher.sha256(json));
        document.setPromptVersion(InsightText.PROMPT_VERSION);
        document.setInputSummary(Document.parse(json));
        document.setContent(content);
        document.setStatus(status);
        document.setCreatedAt(Instant.now(clock));
        document.setBasisTotalProjects(portfolio.totalProjects());
        document.setBasisOverallRoiPercent(portfolio.overallRoiPercent());
        document.setBasisTotalInvestment(portfolio.totalInvestment());
        repository.save(document);
        trim(user.id());
        String source = status == InsightRecordStatus.SUCCESS ? "GEMINI" : "FALLBACK";
        log.info(
                "ai.generate userId={} durationMs={} source={} model={} charCount={}",
                user.id(),
                Duration.ofNanos(System.nanoTime() - startedNanos).toMillis(),
                source,
                model,
                content.length()
        );
        return toResponse(document);
    }

    private void trim(String userId) {
        List<AiInsightDocument> existing = repository.findByRequestedByUserIdAndStatusInOrderByCreatedAtDesc(
                userId,
                List.of(InsightRecordStatus.SUCCESS, InsightRecordStatus.FALLBACK, InsightRecordStatus.ERROR)
        );
        if (existing.size() <= MAX_PER_USER) {
            return;
        }
        repository.deleteAllById(existing.subList(MAX_PER_USER, existing.size()).stream()
                .map(AiInsightDocument::getId)
                .toList());
    }

    private AiInsightResponse toResponse(AiInsightDocument document) {
        String source = document.getStatus() == InsightRecordStatus.SUCCESS ? "GEMINI" : "FALLBACK";
        return new AiInsightResponse(
                document.getId(),
                document.getContent(),
                source,
                InsightText.DISCLAIMER,
                document.getCreatedAt(),
                new InsightBasisResponse(
                        document.getBasisTotalProjects(),
                        document.getBasisOverallRoiPercent(),
                        document.getBasisTotalInvestment()
                )
        );
    }

    private String configuredModel() {
        String model = properties.getModel();
        if (model == null || model.isBlank()) {
            return "gemini-3.6-flash";
        }
        return model.trim();
    }
}
