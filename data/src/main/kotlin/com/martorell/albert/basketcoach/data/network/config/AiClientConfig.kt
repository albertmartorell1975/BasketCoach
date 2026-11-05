package com.martorell.albert.basketcoach.data.network.config

/**
 * Generic configuration parameters for an OpenAI-compatible AI inference client.
 *
 * Supports optional secondary provider chaining via [fallbackConfig] for high-availability
 * failover resilience (e.g. falling back from DevExpert to Google Gemini).
 *
 * @property apiKey Secret API key used for bearer authentication.
 * @property baseUrl The root API endpoint URL (excluding endpoint paths).
 * @property model The inference model identifier (e.g. "chat", "chat-pro", "gemini-2.5-flash").
 * @property fallbackConfig Optional secondary [AiClientConfig] activated when primary inference fails
 * with subscription expiration, authentication, rate limit, server outage, or timeout errors.
 */
data class AiClientConfig(
    val apiKey: String,
    val baseUrl: String,
    val model: String,
    val fallbackConfig: AiClientConfig? = null
) {
    companion object {
        /**
         * Resolves the AI client configuration by prioritizing DevExpert Inference and nesting Google Gemini
         * as a secondary failover provider when both keys are present.
         *
         * Resolution order:
         * 1. If [DevExpertConfig.resolveApiKey] yields a non-blank key:
         *    configures [DevExpertConfig], chaining [fallbackConfig] to [GeminiConfig] if [GeminiConfig.resolveApiKey] is present.
         * 2. If only [GeminiConfig.resolveApiKey] yields a non-blank key:
         *    boots directly using [GeminiConfig] with zero DevExpert dependency.
         * 3. Fallback to an empty API key using [DevExpertConfig] defaults.
         */
        fun fromEnvironment(): AiClientConfig {
            val devexpertKey = DevExpertConfig.resolveApiKey()
            val geminiKey = GeminiConfig.resolveApiKey()
            val geminiConfig = if (!geminiKey.isNullOrBlank()) {
                GeminiConfig.create(apiKey = geminiKey)
            } else {
                null
            }

            if (!devexpertKey.isNullOrBlank()) {
                return DevExpertConfig.create(
                    apiKey = devexpertKey,
                    fallbackConfig = geminiConfig
                )
            }

            if (geminiConfig != null) {
                return geminiConfig
            }

            return DevExpertConfig.create(apiKey = "")
        }
    }
}
