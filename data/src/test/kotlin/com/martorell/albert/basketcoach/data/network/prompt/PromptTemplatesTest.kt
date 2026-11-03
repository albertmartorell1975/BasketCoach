package com.martorell.albert.basketcoach.data.network.prompt

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale

class PromptTemplatesTest {

    @Test
    fun `resolveTargetLanguage returns Spanish when locale is es`() {
        val target = PromptTemplates.resolveTargetLanguage(Locale.forLanguageTag("es-ES"))
        assertEquals("Spanish (Español)", target)
    }

    @Test
    fun `resolveTargetLanguage returns Catalan when locale is ca`() {
        val target = PromptTemplates.resolveTargetLanguage(Locale.forLanguageTag("ca-ES"))
        assertEquals("Catalan (Català)", target)
    }

    @Test
    fun `resolveTargetLanguage defaults to Catalan when locale is English or other`() {
        val targetEnglish = PromptTemplates.resolveTargetLanguage(Locale.ENGLISH)
        assertEquals("Catalan (Català)", targetEnglish)

        val targetGerman = PromptTemplates.resolveTargetLanguage(Locale.GERMAN)
        assertEquals("Catalan (Català)", targetGerman)
    }

    @Test
    fun `buildAnalysisSystemPrompt incorporates target language in language mandate`() {
        val caPrompt = PromptTemplates.buildAnalysisSystemPrompt(Locale.forLanguageTag("ca"))
        assertTrue(caPrompt.contains("LANGUAGE MANDATE: All human-readable text fields in the output JSON"))
        assertTrue(caPrompt.contains("MUST be written in Catalan (Català)"))

        val esPrompt = PromptTemplates.buildAnalysisSystemPrompt(Locale.forLanguageTag("es"))
        assertTrue(esPrompt.contains("MUST be written in Spanish (Español)"))
    }

    @Test
    fun `buildAnalysisUserPrompt incorporates target language`() {
        val caUserPrompt = PromptTemplates.buildAnalysisUserPrompt("Game summary", Locale.forLanguageTag("ca"))
        assertTrue(caUserPrompt.contains("All text fields must be written in Catalan (Català)"))

        val esUserPrompt = PromptTemplates.buildAnalysisUserPrompt("Game summary", Locale.forLanguageTag("es"))
        assertTrue(esUserPrompt.contains("All text fields must be written in Spanish (Español)"))
    }

    @Test
    fun `buildChatSystemPrompt incorporates target language`() {
        val caChatPrompt = PromptTemplates.buildChatSystemPrompt("Game context", Locale.forLanguageTag("ca"))
        assertTrue(caChatPrompt.contains("Always reply in Catalan (Català)"))

        val esChatPrompt = PromptTemplates.buildChatSystemPrompt(null, Locale.forLanguageTag("es"))
        assertTrue(esChatPrompt.contains("Always reply in Spanish (Español)"))
    }
}
