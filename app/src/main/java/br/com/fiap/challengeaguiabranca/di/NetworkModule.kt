package br.com.fiap.challengeaguiabranca.di

import br.com.fiap.challengeaguiabranca.BuildConfig
import br.com.fiap.challengeaguiabranca.data.remote.api.AdviceApiService
import br.com.fiap.challengeaguiabranca.data.remote.api.FakerApiService
import org.koin.core.qualifier.named
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

private const val FAKER_BASE_URL = "https://fakerapi.it/"
private const val ADVICE_BASE_URL = "https://api.adviceslip.com/"

val networkModule = module {

    single {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            isLenient = true
        }
    }

    single {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single(named("fakerRetrofit")) {
        val json = get<Json>()
        val client = get<OkHttpClient>()
        Retrofit.Builder()
            .baseUrl(FAKER_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    single(named("adviceRetrofit")) {
        val json = get<Json>()
        val client = get<OkHttpClient>()
        Retrofit.Builder()
            .baseUrl(ADVICE_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    single {
        get<Retrofit>(named("fakerRetrofit"))
            .create(FakerApiService::class.java)
    }

    single {
        get<Retrofit>(named("adviceRetrofit"))
            .create(AdviceApiService::class.java)
    }
}
