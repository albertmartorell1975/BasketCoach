package com.martorell.albert.basketcoach.data.config

import java.io.File
import java.io.FileInputStream
import java.util.Properties

/**
 * Configuration parameters for connecting to the DevExpert Inference API or Google Gemini fallback.
 *
 * @property apiKey Secret API key used for bearer authentication.
 * @property baseUrl The root API endpoint URL (excluding endpoint paths).
 * @property model The inference model identifier (e.g. "chat", "chat-pro", "gemini-2.5-flash").
 */
data class DevExpertConfig(
    val apiKey: String,
    val baseUrl: String = DEFAULT_BASE_URL,
    val model: String = DEFAULT_MODEL
) {
    companion object {
        const val DEFAULT_BASE_URL: String = "https://inference.devexpert.io/v1"
        const val DEFAULT_MODEL: String = "chat"
        const val MODEL_PRO: String = "chat-pro"

        const val GEMINI_BASE_URL: String = "https://generativelanguage.googleapis.com/v1beta/openai"
        const val GEMINI_DEFAULT_MODEL: String = "gemini-2.5-flash"

        /**
         * Creates a [DevExpertConfig] configured for the Google Gemini OpenAI-compatible endpoint.
         *
         * @param apiKey Google Gemini API key.
         * @param model Model identifier, defaults to "gemini-2.5-flash".
         */
        fun forGemini(apiKey: String, model: String = GEMINI_DEFAULT_MODEL): DevExpertConfig {
            return DevExpertConfig(
                apiKey = apiKey,
                baseUrl = GEMINI_BASE_URL,
                model = model
            )
        }

        /**
         * Resolves configuration from environment variables, system properties, or a `local.properties` file.
         *
         * Priority resolution:
         * 1. Check DEVEXPERT_API_KEY from environment, system properties, or local.properties.
         *    If found and not blank, uses DevExpert Inference defaults.
         * 2. Check GEMINI_API_KEY from environment, system properties, or local.properties.
         *    If found and not blank, uses Gemini defaults.
         * 3. Fallback to an empty API key with DevExpert Inference defaults.
         */
        fun fromEnvironment(): DevExpertConfig {
            val devexpertKey = resolveKey("DEVEXPERT_API_KEY")
            if (!devexpertKey.isNullOrBlank()) {
                return DevExpertConfig(
                    apiKey = devexpertKey,
                    baseUrl = DEFAULT_BASE_URL,
                    model = DEFAULT_MODEL
                )
            }

            val geminiKey = resolveKey("GEMINI_API_KEY")
            if (!geminiKey.isNullOrBlank()) {
                return forGemini(apiKey = geminiKey)
            }

            return DevExpertConfig(
                apiKey = "",
                baseUrl = DEFAULT_BASE_URL,
                model = DEFAULT_MODEL
            )
        }

        private fun resolveKey(keyName: String): String? {
            System.getenv(keyName)?.takeIf { it.isNotBlank() }?.let { return it }
            System.getProperty(keyName)?.takeIf { it.isNotBlank() }?.let { return it }

            val localPropsFiles = listOf(
                File("local.properties"),
                File("../local.properties"),
                File("../../local.properties")
            )
            for (file in localPropsFiles) {
                if (file.exists() && file.isFile) {
                    try {
                        val props = Properties()
                        FileInputStream(file).use { props.load(it) }
                        val propValue = props.getProperty(keyName)
                        if (!propValue.isNullOrBlank()) {
                            return propValue.trim()
                        }
                    } catch (_: Exception) {
                        // ignore and try next source
                    }
                }
            }
            return null
        }
    }
}
