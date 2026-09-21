package br.com.fiap.aguiabranca.notification.api.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Lista curta de notificações. unreadHint é true quando há itens; o app controla lido em memória.")
public record NotificationsResponse(
        List<NotificationItemResponse> items,
        boolean unreadHint
) {
}
