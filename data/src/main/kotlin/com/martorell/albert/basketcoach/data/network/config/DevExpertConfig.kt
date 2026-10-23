package com.martorell.albert.basketcoach.data.network.config

/**
 * Provider-specific configuration definitions and resolution helpers for DevExpert Inference.
 */
object DevExpertConfig {

    const val ENV_API_KEY: String = "DEVEXPERT_API_KEY"
    const val DEFAULT_BASE_URL: String = "https://inference.devexpert.io/v1"
    const val DEFAULT_MODEL: String = "chat"
    const val MODEL_PRO: String = "chat-pro"

    /**
     * Creates an [AiClientConfig] pointing to the DevExpert Inference service.
     *
     * @param apiKey DevExpert secret API key.
     * @param baseUrl DevExpert root endpoint URL (default is [DEFAULT_BASE_URL]).
     * @param model Model alias (default is [DEFAULT_MODEL]).
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
     * Resolves the DevExpert API key by checking environment variables, system properties, and `local.properties`.
     */
    fun resolveApiKey(): String? {
        System.getenv(ENV_API_KEY)?.takeIf { it.isNotBlank() }?.let { return it }
        System.getProperty(ENV_API_KEY)?.takeIf { it.isNotBlank() }?.let { return it }
        return LocalPropertiesReader.getProperty(ENV_API_KEY)
    }
}
