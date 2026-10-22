package com.martorell.albert.basketcoach.data.network.config

/**
 * Provider-specific configuration definitions and resolution helpers for Google Gemini's OpenAI-compatible endpoint.
 */
object GeminiConfig {

    const val ENV_API_KEY: String = "GEMINI_API_KEY"
    const val DEFAULT_BASE_URL: String = "https://generativelanguage.googleapis.com/v1beta/openai"
    const val DEFAULT_MODEL: String = "gemini-2.5-flash"

    /**
     * Creates an [AiClientConfig] pointing to the Google Gemini OpenAI-compatible endpoint.
     *
     * @param apiKey Google Gemini secret API key.
     * @param baseUrl Gemini OpenAI-compatible root URL (default is [DEFAULT_BASE_URL]).
     * @param model Model identifier (default is [DEFAULT_MODEL]).
     */
    fun create(
        apiKey: String,
        baseUrl: String = DEFAULT_BASE_URL,
        model: String = DEFAULT_MODEL
    ): AiClientConfig {
        return AiClientConfig(
            apiKey = apiKey,
            baseUrl = baseUrl,
            model = model
        )
    }

    /**
     * Resolves the Gemini API key by checking environment variables, system properties, and `local.properties`.
     */
    fun resolveApiKey(): String? {
        System.getenv(ENV_API_KEY)?.takeIf { it.isNotBlank() }?.let { return it }
        System.getProperty(ENV_API_KEY)?.takeIf { it.isNotBlank() }?.let { return it }
        return LocalPropertiesReader.getProperty(ENV_API_KEY)
    }
}
