package br.com.fiap.challengeaguiabranca.domain.usecase.notification

import br.com.fiap.challengeaguiabranca.domain.model.ManagerNotification
import br.com.fiap.challengeaguiabranca.domain.model.OperatorNotification
import br.com.fiap.challengeaguiabranca.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow

class ObserveOperatorNotificationsUseCase(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(): Flow<List<OperatorNotification>> =
        notificationRepository.observeForOperator()
}

class ObserveManagementNotificationsUseCase(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(): Flow<List<ManagerNotification>> =
        notificationRepository.observeForManagement()
}
