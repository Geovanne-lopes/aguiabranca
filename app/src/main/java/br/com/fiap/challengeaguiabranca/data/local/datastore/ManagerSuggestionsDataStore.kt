package br.com.fiap.challengeaguiabranca.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import br.com.fiap.challengeaguiabranca.domain.model.ManagerSuggestion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.managerSuggestionsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "manager_suggestions"
)

@Serializable
private data class StoredSuggestion(
    val id: String,
    val managerName: String,
    val targetAuthorId: String,
    val targetEmail: String,
    val targetName: String,
    val message: String,
    val createdAtEpochMillis: Long
)

class ManagerSuggestionsDataStore(
    private val context: Context
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val key = stringPreferencesKey("suggestions_json")

    fun observeAll(): Flow<List<ManagerSuggestion>> =
        context.managerSuggestionsDataStore.data.map { prefs ->
            decode(prefs[key])
        }

    suspend fun insert(suggestion: ManagerSuggestion) {
        context.managerSuggestionsDataStore.edit { prefs ->
            val current = decode(prefs[key]).toMutableList()
            current.add(0, suggestion)
            prefs[key] = encode(current)
        }
    }

    private fun decode(raw: String?): List<ManagerSuggestion> {
        if (raw.isNullOrBlank()) return emptyList()
        return runCatching {
            json.decodeFromString<List<StoredSuggestion>>(raw).map { it.toDomain() }
        }.getOrDefault(emptyList())
    }

    private fun encode(list: List<ManagerSuggestion>): String =
        json.encodeToString(list.map { it.toStored() })

    private fun StoredSuggestion.toDomain() = ManagerSuggestion(
        id = id,
        managerName = managerName,
        targetAuthorId = targetAuthorId,
        targetEmail = targetEmail,
        targetName = targetName,
        message = message,
        createdAtEpochMillis = createdAtEpochMillis
    )

    private fun ManagerSuggestion.toStored() = StoredSuggestion(
        id = id,
        managerName = managerName,
        targetAuthorId = targetAuthorId,
        targetEmail = targetEmail,
        targetName = targetName,
        message = message,
        createdAtEpochMillis = createdAtEpochMillis
    )
}
