package br.com.fiap.challengeaguiabranca.di

import br.com.fiap.challengeaguiabranca.BuildConfig
import br.com.fiap.challengeaguiabranca.data.remote.ApiCaller
import br.com.fiap.challengeaguiabranca.data.remote.api.InnovationApi
import br.com.fiap.challengeaguiabranca.data.remote.auth.AuthInterceptor
import br.com.fiap.challengeaguiabranca.data.remote.auth.SessionExpiry
import br.com.fiap.challengeaguiabranca.data.remote.auth.TokenAuthenticator
import br.com.fiap.challengeaguiabranca.data.local.datastore.TokenStore
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

const val PUBLIC_API = "publicInnovationApi"
const val SECURED_API = "securedInnovationApi"

val networkModule = module {

    single {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            isLenient = true
            explicitNulls = false
        }
    }

    single { TokenStore(androidContext()) }

    single { SessionExpiry(get(), get()) }

    single { ApiCaller(get()) }

    single(named("publicHttp")) {
        OkHttpClient.Builder()
            .addInterceptor(httpLogging())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single(named(PUBLIC_API)) {
        retrofit(get<Json>(), get(named("publicHttp")), apiBaseUrl()).create(InnovationApi::class.java)
    }

    single {
        TokenAuthenticator(
            tokenStore = get(),
            publicApi = get(named(PUBLIC_API)),
            sessionExpiry = get()
        )
    }

    single(named("securedHttp")) {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(get<TokenStore>()))
            .authenticator(get<TokenAuthenticator>())
            .addInterceptor(httpLogging())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single(named(SECURED_API)) {
        retrofit(get<Json>(), get(named("securedHttp")), apiBaseUrl()).create(InnovationApi::class.java)
    }
}

private fun apiBaseUrl(): String {
    val configured = BuildConfig.API_BASE_URL
    return if (configured.endsWith("/")) configured else "$configured/"
}

private fun httpLogging(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BASIC
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }
}

private fun retrofit(json: Json, client: OkHttpClient, baseUrl: String): Retrofit {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
}
