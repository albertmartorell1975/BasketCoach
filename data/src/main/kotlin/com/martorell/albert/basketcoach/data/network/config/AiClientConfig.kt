package com.martorell.albert.basketcoach.data.network.config

/**
 * Generic configuration parameters for an OpenAI-compatible AI inference client.
 *
 * @property apiKey Secret API key used for bearer authentication.
 * @property baseUrl The root API endpoint URL (excluding endpoint paths).
 * @property model The inference model identifier (e.g. "chat", "chat-pro", "gemini-2.5-flash").
 */
data class AiClientConfig(
    val apiKey: String,
    val baseUrl: String,
    val model: String
) {
    companion object {
        /**
         * Resolves the AI client configuration by prioritizing DevExpert Inference and falling back to Google Gemini.
         *
         * Resolution order:
         * 1. If [DevExpertConfig.resolveApiKey] yields a non-blank key, configures [DevExpertConfig].
         * 2. If [GeminiConfig.resolveApiKey] yields a non-blank key, configures [GeminiConfig].
         * 3. Fallback to an empty API key using [DevExpertConfig] defaults.
         */
        fun fromEnvironment(): AiClientConfig {
            val devexpertKey = DevExpertConfig.resolveApiKey()
            if (!devexpertKey.isNullOrBlank()) {
                return DevExpertConfig.create(apiKey = devexpertKey)
            }

            val geminiKey = GeminiConfig.resolveApiKey()
            if (!geminiKey.isNullOrBlank()) {
                return GeminiConfig.create(apiKey = geminiKey)
            }

            return DevExpertConfig.create(apiKey = "")
        }
    }
}
