package br.com.fiap.aguiabranca.notification.application;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.fiap.aguiabranca.guideline.infra.GuidelineDocument;
import br.com.fiap.aguiabranca.idea.domain.IdeaStatus;
import br.com.fiap.aguiabranca.idea.infra.IdeaDocument;
import br.com.fiap.aguiabranca.notification.api.dto.NotificationItemResponse;
import br.com.fiap.aguiabranca.notification.domain.NotificationType;
import br.com.fiap.aguiabranca.suggestion.infra.SuggestionDocument;

public final class NotificationProjector {

    public static final int MAX_ITEMS = 50;

    private NotificationProjector() {
    }

    public static List<NotificationItemResponse> forOperator(
            List<GuidelineDocument> guidelines,
            List<SuggestionDocument> received,
            List<IdeaDocument> ownIdeas
    ) {
        List<NotificationItemResponse> items = new ArrayList<>();
        for (GuidelineDocument guideline : guidelines) {
            items.add(new NotificationItemResponse(
                    "guideline-" + guideline.getId(),
                    "Orientação da liderança",
                    "A liderança publicou: \"" + guideline.getTitle() + "\". " + guideline.getContent(),
                    NotificationType.GUIDELINE,
                    firstNonNull(guideline.getUpdatedAt(), guideline.getCreatedAt())
            ));
        }
        for (SuggestionDocument suggestion : received) {
            items.add(new NotificationItemResponse(
                    "suggestion-" + suggestion.getId(),
                    "Sugestão do gestor",
                    suggestion.getAuthorName() + " sugeriu: " + suggestion.getMessage(),
                    NotificationType.SUGGESTION,
                    suggestion.getCreatedAt()
            ));
        }
        for (IdeaDocument idea : ownIdeas) {
            if (idea.getStatus() == null) {
                continue;
            }
            IdeaCopy copy = ideaCopy(idea);
            items.add(new NotificationItemResponse(
                    "idea-" + idea.getStatus().name().toLowerCase(Locale.ROOT) + "-" + idea.getId(),
                    copy.title(),
                    copy.body(),
                    copy.type(),
                    firstNonNull(idea.getUpdatedAt(), idea.getCreatedAt())
            ));
        }
        return finish(items);
    }

    public static List<NotificationItemResponse> forManager(
            List<IdeaDocument> ideas,
            List<SuggestionDocument> sentSuggestions,
            Map<String, String> names,
            Set<String> operatorIds,
            Instant now
    ) {
        List<NotificationItemResponse> items = new ArrayList<>();
        List<IdeaDocument> pending = ideas.stream()
                .filter(idea -> idea.getStatus() == IdeaStatus.PENDING)
                .sorted(byCreatedAtDesc())
                .toList();
        if (!pending.isEmpty()) {
            items.add(new NotificationItemResponse(
                    "pending-" + pending.size(),
                    "Ideias aguardando curadoria",
                    "Você tem " + pending.size() + " ideia(s) pendente(s) para avaliar na aba Curadoria.",
                    NotificationType.PENDING_IDEAS,
                    now
            ));
        }
        pending.stream().limit(5).forEach(idea -> items.add(new NotificationItemResponse(
                "new-" + idea.getId(),
                "Nova ideia de " + nameOf(names, idea.getAuthorId()),
                "\"" + idea.getTitle() + "\" — aguarda sua avaliação.",
                NotificationType.NEW_IDEA,
                idea.getCreatedAt()
        )));
        sentSuggestions.stream()
                .sorted(Comparator.comparing(SuggestionDocument::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(3)
                .forEach(suggestion -> items.add(new NotificationItemResponse(
                        "suggestion-" + suggestion.getId(),
                        "Sugestão enviada",
                        "Para " + suggestion.getTargetName() + ": " + suggestion.getMessage(),
                        NotificationType.SUGGESTION_SENT,
                        suggestion.getCreatedAt()
                )));
        ideas.stream()
                .filter(idea -> operatorIds.contains(idea.getAuthorId()))
                .collect(Collectors.groupingBy(IdeaDocument::getAuthorId, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Comparator.comparingLong(Map.Entry<String, Long>::getValue).thenComparing(Map.Entry::getKey))
                .ifPresent(top -> items.add(new NotificationItemResponse(
                        "top-" + top.getKey(),
                        "Operador mais ativo",
                        nameOf(names, top.getKey()) + " lidera com " + top.getValue() + " ideia(s) enviada(s).",
                        NotificationType.TEAM_ACTIVITY,
                        now.minus(Duration.ofHours(1))
                )));
        return finish(items);
    }

    public static List<NotificationItemResponse> forLeader(
            List<IdeaDocument> ideas,
            List<GuidelineDocument> guidelines,
            Map<String, String> names,
            Instant now
    ) {
        List<NotificationItemResponse> items = new ArrayList<>();
        long pending = ideas.stream().filter(idea -> idea.getStatus() == IdeaStatus.PENDING).count();
        long approved = ideas.stream().filter(idea ->
                idea.getStatus() == IdeaStatus.APPROVED || idea.getStatus() == IdeaStatus.PRIORITIZED).count();
        items.add(new NotificationItemResponse(
                "overview-" + ideas.size() + "-" + pending + "-" + approved,
                "Visão estratégica",
                ideas.size() + " ideias no portfólio · " + pending + " pendentes · " + approved + " aprovadas.",
                NotificationType.TEAM_ACTIVITY,
                now
        ));

        Instant startOfMonth = now.atZone(ZoneOffset.UTC).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS).toInstant();
        ideas.stream()
                .filter(idea -> idea.getAuthorId() != null && idea.getCreatedAt() != null && !idea.getCreatedAt().isBefore(startOfMonth))
                .collect(Collectors.groupingBy(IdeaDocument::getAuthorId, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Comparator.comparingLong(Map.Entry<String, Long>::getValue).thenComparing(Map.Entry::getKey))
                .ifPresent(top -> items.add(new NotificationItemResponse(
                        "monthly-" + top.getKey() + "-" + top.getValue(),
                        "Destaque do mês",
                        nameOf(names, top.getKey()) + " enviou " + top.getValue() + " ideia(s) neste mês.",
                        NotificationType.TEAM_ACTIVITY,
                        now.minus(Duration.ofSeconds(60))
                )));

        ideas.stream()
                .filter(idea -> idea.getStatus() == IdeaStatus.PENDING)
                .sorted(byCreatedAtDesc())
                .limit(3)
                .forEach(idea -> items.add(new NotificationItemResponse(
                        "idea-" + idea.getId(),
                        "Ideia pendente de curadoria",
                        "\"" + idea.getTitle() + "\" — " + nameOf(names, idea.getAuthorId()),
                        NotificationType.NEW_IDEA,
                        idea.getCreatedAt()
                )));

        guidelines.stream()
                .sorted(Comparator.comparing(
                        (GuidelineDocument guideline) -> firstNonNull(guideline.getUpdatedAt(), guideline.getCreatedAt()),
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .limit(2)
                .forEach(guideline -> items.add(new NotificationItemResponse(
                        "guideline-" + guideline.getId(),
                        "Diretriz publicada",
                        guideline.getTitle(),
                        NotificationType.SUGGESTION_SENT,
                        firstNonNull(guideline.getUpdatedAt(), guideline.getCreatedAt())
                )));
        return finish(items);
    }

    private static IdeaCopy ideaCopy(IdeaDocument idea) {
        String title = idea.getTitle();
        return switch (idea.getStatus()) {
            case PENDING -> new IdeaCopy(
                    "Ideia enviada",
                    "Sua ideia \"" + title + "\" está aguardando curadoria do gestor.",
                    NotificationType.IDEA_PENDING
            );
            case APPROVED -> new IdeaCopy(
                    "Ideia aprovada",
                    "Parabéns! \"" + title + "\" foi aprovada. Confira na aba Ideias.",
                    NotificationType.IDEA_APPROVED
            );
            case REJECTED -> new IdeaCopy(
                    "Ideia reprovada",
                    "A ideia \"" + title + "\" foi reprovada. Você pode enviar uma nova versão.",
                    NotificationType.IDEA_REJECTED
            );
            case PRIORITIZED -> new IdeaCopy(
                    "Ideia priorizada",
                    "Sua ideia \"" + title + "\" foi priorizada pela gestão.",
                    NotificationType.IDEA_PRIORITIZED
            );
        };
    }

    private static List<NotificationItemResponse> finish(List<NotificationItemResponse> items) {
        List<NotificationItemResponse> sorted = new ArrayList<>(items);
        sorted.sort(Comparator.comparing(NotificationItemResponse::createdAt, Comparator.nullsLast(Comparator.reverseOrder())));
        if (sorted.size() <= MAX_ITEMS) {
            return List.copyOf(sorted);
        }
        return List.copyOf(sorted.subList(0, MAX_ITEMS));
    }

    private static Comparator<IdeaDocument> byCreatedAtDesc() {
        return Comparator.comparing(IdeaDocument::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
    }

    private static String nameOf(Map<String, String> names, String userId) {
        String name = names.get(userId);
        if (name == null || name.isBlank()) {
            return "Colaborador";
        }
        return name;
    }

    private static Instant firstNonNull(Instant preferred, Instant fallback) {
        return preferred != null ? preferred : fallback;
    }

    private record IdeaCopy(String title, String body, NotificationType type) {
    }
}
