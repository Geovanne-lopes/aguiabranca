package br.com.fiap.aguiabranca.notification.api.dto;

import java.time.Instant;

import br.com.fiap.aguiabranca.notification.domain.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Item de notificação projetado. createdAt da ideia usa updatedAt (momento da decisão).")
public record NotificationItemResponse(
        String id,
        String title,
        String body,
        NotificationType type,
        Instant createdAt
) {
}
