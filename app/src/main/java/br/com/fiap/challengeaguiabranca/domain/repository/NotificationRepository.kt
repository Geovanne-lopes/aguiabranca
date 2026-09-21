package br.com.fiap.challengeaguiabranca.domain.repository

import br.com.fiap.challengeaguiabranca.domain.model.ManagerNotification
import br.com.fiap.challengeaguiabranca.domain.model.OperatorNotification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun observeForOperator(): Flow<List<OperatorNotification>>
    fun observeForManagement(): Flow<List<ManagerNotification>>
}
