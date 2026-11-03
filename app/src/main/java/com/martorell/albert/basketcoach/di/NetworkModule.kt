package com.martorell.albert.basketcoach.di

import com.martorell.albert.basketcoach.BuildConfig
import com.martorell.albert.basketcoach.data.network.config.AiClientConfig
import com.martorell.albert.basketcoach.data.network.config.DevExpertConfig
import com.martorell.albert.basketcoach.data.network.config.GeminiConfig
import com.martorell.albert.basketcoach.data.source.remote.AiRemoteDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
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

    /**
     * Provides the application-wide [AiClientConfig] singleton.
     *
     * Resolves API keys from [BuildConfig] (populated at build time) with fallback to environment variables.
     * Prioritizes DevExpert Inference, falling back to Google Gemini.
     *
     * @return Initialized [AiClientConfig] instance.
     */
    @Provides
    @Singleton
    fun provideAiClientConfig(): AiClientConfig {
        val devexpertKey = BuildConfig.DEVEXPERT_API_KEY.takeIf { it.isNotBlank() }
            ?: DevExpertConfig.resolveApiKey()

        if (!devexpertKey.isNullOrBlank()) {
            return DevExpertConfig.create(apiKey = devexpertKey)
        }

        val geminiKey = BuildConfig.GEMINI_API_KEY.takeIf { it.isNotBlank() }
            ?: GeminiConfig.resolveApiKey()

        if (!geminiKey.isNullOrBlank()) {
            return GeminiConfig.create(apiKey = geminiKey)
        }

        return DevExpertConfig.create(apiKey = "")
    }

    /**
     * Provides the default [OkHttpClient] singleton configured with appropriate timeouts for AI inference.
     *
     * @return Configured [OkHttpClient] instance.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = AiRemoteDataSourceImpl.defaultOkHttpClient()

    /**
     * Provides the default [Json] serialization engine configured with permissive decoding rules.
     *
     * @return Configured [Json] instance.
     */
    @Provides
    @Singleton
    fun provideJson(): Json = AiRemoteDataSourceImpl.defaultJson()
}
