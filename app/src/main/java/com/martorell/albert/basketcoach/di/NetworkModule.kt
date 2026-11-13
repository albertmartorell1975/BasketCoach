package com.martorell.albert.basketcoach.di

import com.martorell.albert.basketcoach.BuildConfig
import com.martorell.albert.basketcoach.data.network.config.AiClientConfig
import com.martorell.albert.basketcoach.data.network.config.DevExpertConfig
import com.martorell.albert.basketcoach.data.network.config.GeminiConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module provisioning network dependencies and AI configuration in [SingletonComponent].
 *
 * Exposes application-level singletons for [AiClientConfig], [OkHttpClient], and [Json] serializer,
 * utilized by remote AI data sources.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val PLACEHOLDER_DEVEXPERT = "DEFAULT_DEVEXPERT_API_KEY"
    private const val PLACEHOLDER_GEMINI = "DEFAULT_GEMINI_API_KEY"
    private const val DEFAULT_CONNECT_TIMEOUT_SECONDS = 60L
    private const val DEFAULT_READ_TIMEOUT_SECONDS = 60L
    private const val DEFAULT_WRITE_TIMEOUT_SECONDS = 30L

    /**
     * Provides the application-wide [AiClientConfig] singleton.
     *
     * Resolves API keys from [BuildConfig] (populated via secrets-gradle-plugin at build time)
     * with fallback to environment variables. Filters out placeholder defaults ([PLACEHOLDER_DEVEXPERT],
     * [PLACEHOLDER_GEMINI]).
     *
     * Dual-provider resolution order:
     * 1. If DevExpert key is present: configures [DevExpertConfig] as primary, nesting [GeminiConfig]
     *    as [AiClientConfig.fallbackConfig] if a Gemini key is also available.
     * 2. If DevExpert key is absent/placeholder and Gemini key is present: boots directly using [GeminiConfig].
     * 3. Fallback to an empty API key using [DevExpertConfig] defaults with explicit null fallback.
     *
     * @return Initialized [AiClientConfig] instance with fallback chaining.
     */
    @Provides
    @Singleton
    fun provideAiClientConfig(): AiClientConfig {
        val devExpertKey = BuildConfig.DEVEXPERT_API_KEY
            .takeIf { it.isNotBlank() && it != PLACEHOLDER_DEVEXPERT }
            ?: DevExpertConfig.resolveApiKey()

        val geminiKey = BuildConfig.GEMINI_API_KEY
            .takeIf { it.isNotBlank() && it != PLACEHOLDER_GEMINI }
            ?: GeminiConfig.resolveApiKey()

        val geminiConfig = if (!geminiKey.isNullOrBlank()) {
            GeminiConfig.create(apiKey = geminiKey)
        } else {
            null
        }

        if (!devExpertKey.isNullOrBlank()) {
            return DevExpertConfig.create(
                apiKey = devExpertKey,
                fallbackConfig = geminiConfig
            )
        }

        if (geminiConfig != null) {
            return geminiConfig
        }

        return DevExpertConfig.create(
            apiKey = "",
            fallbackConfig = null
        )
    }

    /**
     * Provides the default [OkHttpClient] singleton configured with resilient timeouts for AI inference.
     *
     * @return Configured [OkHttpClient] instance.
     */
    @Provides
    @Singleton
    fun provideAiOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(DEFAULT_CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Provides the default [Json] serialization engine configured with permissive decoding rules.
     *
     * @return Configured [Json] instance.
     */
    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
        }
    }
}
