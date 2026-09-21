package br.com.fiap.aguiabranca.notification.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import br.com.fiap.aguiabranca.guideline.infra.GuidelineDocument;
import br.com.fiap.aguiabranca.idea.domain.IdeaStatus;
import br.com.fiap.aguiabranca.idea.infra.IdeaDocument;
import br.com.fiap.aguiabranca.notification.api.dto.NotificationItemResponse;
import br.com.fiap.aguiabranca.notification.domain.NotificationType;
import br.com.fiap.aguiabranca.suggestion.infra.SuggestionDocument;

class NotificationProjectorTest {

    private static final Instant NOW = Instant.parse("2026-09-21T15:00:00Z");

    @Test
    void operatorUsesUpdatedAtAndSuggestionType() {
        IdeaDocument idea = idea("idea-1", "op-1", IdeaStatus.APPROVED, "App fila",
                Instant.parse("2026-09-01T10:00:00Z"), Instant.parse("2026-09-21T12:00:00Z"));
        SuggestionDocument suggestion = suggestion("sug-1", "Carlos Gestor", "op-1", "Ana Operadora",
                "Priorize a fila digital.", Instant.parse("2026-09-20T10:00:00Z"));
        GuidelineDocument guideline = guideline("gl-1", "Transformação Digital", "Digitalize o balcão.",
                Instant.parse("2026-09-19T10:00:00Z"));

        List<NotificationItemResponse> items = NotificationProjector.forOperator(
                List.of(guideline), List.of(suggestion), List.of(idea));

        NotificationItemResponse approved = items.stream()
                .filter(item -> item.type() == NotificationType.IDEA_APPROVED)
                .findFirst()
                .orElseThrow();
        assertThat(approved.id()).isEqualTo("idea-approved-idea-1");
        assertThat(approved.createdAt()).isEqualTo(Instant.parse("2026-09-21T12:00:00Z"));
        assertThat(approved.body()).contains("App fila");

        NotificationItemResponse received = items.stream()
                .filter(item -> item.id().equals("suggestion-sug-1"))
                .findFirst()
                .orElseThrow();
        assertThat(received.type()).isEqualTo(NotificationType.SUGGESTION);
        assertThat(received.body()).isEqualTo("Carlos Gestor sugeriu: Priorize a fila digital.");
        assertThat(items).allMatch(item -> item.type() == NotificationType.GUIDELINE
                || item.type() == NotificationType.SUGGESTION
                || item.type().name().startsWith("IDEA_"));
    }

    @Test
    void managerUsesRealOperatorNameAndCapsPendingIdeas() {
        List<IdeaDocument> ideas = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            ideas.add(idea("p-" + i, "op-1", IdeaStatus.PENDING, "Pendente " + i,
                    Instant.parse("2026-09-21T10:00:0" + Math.min(i, 9) + "Z"), null));
        }
        ideas.add(idea("m-1", "mgr-1", IdeaStatus.PENDING, "Do gestor",
                Instant.parse("2026-09-21T11:00:00Z"), null));
        ideas.add(idea("m-2", "mgr-1", IdeaStatus.APPROVED, "Outra do gestor",
                Instant.parse("2026-09-21T11:01:00Z"), null));

        List<NotificationItemResponse> items = NotificationProjector.forManager(
                ideas,
                List.of(suggestion("sug-1", "Carlos Gestor", "op-1", "Ana Operadora", "Mensagem válida.", NOW)),
                Map.of("op-1", "Ana Operadora", "mgr-1", "Carlos Gestor"),
                Set.of("op-1"),
                NOW
        );

        assertThat(items.stream().filter(item -> item.type() == NotificationType.NEW_IDEA)).hasSize(5);
        assertThat(items).anyMatch(item -> item.type() == NotificationType.PENDING_IDEAS
                && item.body().contains("7 ideia(s)"));
        NotificationItemResponse top = items.stream()
                .filter(item -> item.type() == NotificationType.TEAM_ACTIVITY)
                .findFirst()
                .orElseThrow();
        assertThat(top.title()).isEqualTo("Operador mais ativo");
        assertThat(top.body()).isEqualTo("Ana Operadora lidera com 6 ideia(s) enviada(s).");
        assertThat(items).noneMatch(item -> item.body().contains("mock-op") || item.title().contains("mock-op"));
        assertThat(items).anyMatch(item -> item.type() == NotificationType.SUGGESTION_SENT
                && item.body().startsWith("Para Ana Operadora:"));
    }

    @Test
    void leaderOverviewHighlightAndRecentGuidelines() {
        IdeaDocument august = idea("old", "op-1", IdeaStatus.APPROVED, "Antiga",
                Instant.parse("2026-08-31T23:00:00Z"), null);
        IdeaDocument september = idea("new", "op-2", IdeaStatus.PENDING, "Do mês",
                Instant.parse("2026-09-02T10:00:00Z"), null);
        GuidelineDocument older = guideline("g1", "Antiga diretriz", "conteúdo", Instant.parse("2026-09-01T00:00:00Z"));
        GuidelineDocument newer = guideline("g2", "Diretriz nova", "conteúdo", Instant.parse("2026-09-20T00:00:00Z"));
        GuidelineDocument newest = guideline("g3", "Diretriz mais nova", "conteúdo", Instant.parse("2026-09-21T00:00:00Z"));

        List<NotificationItemResponse> items = NotificationProjector.forLeader(
                List.of(august, september),
                List.of(older, newer, newest),
                Map.of("op-1", "Ana Operadora", "op-2", "Bruno Operador"),
                NOW
        );

        assertThat(items).anyMatch(item -> item.title().equals("Visão estratégica")
                && item.body().equals("2 ideias no portfólio · 1 pendentes · 1 aprovadas."));
        assertThat(items).anyMatch(item -> item.title().equals("Destaque do mês")
                && item.body().equals("Bruno Operador enviou 1 ideia(s) neste mês."));
        assertThat(items.stream().filter(item -> item.title().equals("Diretriz publicada")).map(NotificationItemResponse::body))
                .containsExactly("Diretriz mais nova", "Diretriz nova");
        assertThat(items.stream().filter(item -> item.type() == NotificationType.NEW_IDEA)).hasSize(1);
    }

    @Test
    void capsAtFiftyItems() {
        List<GuidelineDocument> guidelines = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            guidelines.add(guideline("g-" + i, "Título " + i, "Conteúdo " + i, NOW.minusSeconds(i)));
        }

        List<NotificationItemResponse> items = NotificationProjector.forOperator(guidelines, List.of(), List.of());

        assertThat(items).hasSize(NotificationProjector.MAX_ITEMS);
    }

    private static IdeaDocument idea(
            String id,
            String authorId,
            IdeaStatus status,
            String title,
            Instant createdAt,
            Instant updatedAt
    ) {
        IdeaDocument idea = new IdeaDocument();
        idea.setId(id);
        idea.setAuthorId(authorId);
        idea.setStatus(status);
        idea.setTitle(title);
        idea.setCreatedAt(createdAt);
        idea.setUpdatedAt(updatedAt);
        return idea;
    }

    private static SuggestionDocument suggestion(
            String id,
            String authorName,
            String targetUserId,
            String targetName,
            String message,
            Instant createdAt
    ) {
        SuggestionDocument suggestion = new SuggestionDocument();
        suggestion.setId(id);
        suggestion.setAuthorName(authorName);
        suggestion.setTargetUserId(targetUserId);
        suggestion.setTargetName(targetName);
        suggestion.setMessage(message);
        suggestion.setCreatedAt(createdAt);
        return suggestion;
    }

    private static GuidelineDocument guideline(String id, String title, String content, Instant updatedAt) {
        GuidelineDocument guideline = new GuidelineDocument();
        guideline.setId(id);
        guideline.setTitle(title);
        guideline.setContent(content);
        guideline.setUpdatedAt(updatedAt);
        return guideline;
    }
}
