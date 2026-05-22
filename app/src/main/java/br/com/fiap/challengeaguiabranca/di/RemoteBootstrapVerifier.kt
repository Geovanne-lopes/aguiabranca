package br.com.fiap.challengeaguiabranca.di

import android.util.Log
import br.com.fiap.challengeaguiabranca.domain.repository.InsightRepository
import br.com.fiap.challengeaguiabranca.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Smoke test de rede em DEBUG — validar Etapa 2 via Logcat (tag: RemoteBootstrap).
 */
object RemoteBootstrapVerifier {

    private const val TAG = "RemoteBootstrap"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun verify(
        userRepository: UserRepository,
        insightRepository: InsightRepository
    ) {
        scope.launch {
            runCatching {
                val users = userRepository.fetchSeedUsers()
                Log.d(TAG, "FakerAPI OK — usuários: ${users.size}")
                users.forEach { user ->
                    Log.d(TAG, "  ${user.role}: ${user.name} <${user.email}>")
                }
                val insight = insightRepository.fetchDailyInsight()
                Log.d(TAG, "AdviceSlip OK — insight #${insight.id}: ${insight.message}")
            }.onFailure { error ->
                Log.e(TAG, "Falha na verificação de APIs", error)
            }
        }
    }
}
