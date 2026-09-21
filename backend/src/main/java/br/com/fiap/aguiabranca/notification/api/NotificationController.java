package br.com.fiap.aguiabranca.notification.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.aguiabranca.notification.api.dto.NotificationsResponse;
import br.com.fiap.aguiabranca.notification.application.NotificationService;
import br.com.fiap.aguiabranca.security.current.CurrentUserAccessor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications")
@SecurityRequirement(name = "bearer-jwt")
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUserAccessor currentUserAccessor;

    public NotificationController(NotificationService notificationService, CurrentUserAccessor currentUserAccessor) {
        this.notificationService = notificationService;
        this.currentUserAccessor = currentUserAccessor;
    }

    @GetMapping
    @Operation(summary = "Notificações do papel autenticado, montadas na hora (máx. 50). Sem coleção e sem estado de leitura.")
    public NotificationsResponse list() {
        return notificationService.list(currentUserAccessor.require());
    }
}
