package br.com.fiap.challengeaguiabranca.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import br.com.fiap.challengeaguiabranca.data.remote.auth.AccessTokenSource
import kotlinx.coroutines.flow.first

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class TokenStore(context: Context) : AccessTokenSource {

    private val dataStore = context.authDataStore

    @Volatile
    private var access: String? = null

    @Volatile
    private var refresh: String? = null

    override fun currentAccessToken(): String? = access

    fun currentRefreshToken(): String? = refresh

    suspend fun warmUp() {
        val prefs = dataStore.data.first()
        access = prefs[ACCESS]
        refresh = prefs[REFRESH]
    }

    suspend fun save(accessToken: String, refreshToken: String) {
        access = accessToken
        refresh = refreshToken
        dataStore.edit { prefs ->
            prefs[ACCESS] = accessToken
            prefs[REFRESH] = refreshToken
        }
    }

    fun clearMemory() {
        access = null
        refresh = null
    }

    suspend fun clear() {
        clearMemory()
        dataStore.edit { prefs ->
            prefs.remove(ACCESS)
            prefs.remove(REFRESH)
        }
    }

    private companion object {
        val ACCESS = stringPreferencesKey("access_token")
        val REFRESH = stringPreferencesKey("refresh_token")
    }
}
