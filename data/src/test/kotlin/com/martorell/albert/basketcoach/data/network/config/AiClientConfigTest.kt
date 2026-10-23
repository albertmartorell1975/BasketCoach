package com.martorell.albert.basketcoach.data.network.config

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class AiClientConfigTest {

    @Test
    fun `DevExpertConfig create produces correct default values`() {
        val config = DevExpertConfig.create(apiKey = "devexpert-key")

        assertEquals("devexpert-key", config.apiKey)
        assertEquals("https://inference.devexpert.io/v1", config.baseUrl)
        assertEquals("chat", config.model)
    }

    @Test
    fun `GeminiConfig create produces correct default values`() {
        val config = GeminiConfig.create(apiKey = "gemini-key")

        assertEquals("gemini-key", config.apiKey)
        assertEquals("https://generativelanguage.googleapis.com/v1beta/openai", config.baseUrl)
        assertEquals("gemini-2.5-flash", config.model)
    }

    @Test
    fun `AiClientConfig fromEnvironment returns non-null configuration`() {
        val config = AiClientConfig.fromEnvironment()
        assertNotNull(config)
        assertNotNull(config.baseUrl)
        assertNotNull(config.model)
    }
}
