package com.martorell.albert.basketcoach.data.network.prompt

import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.InsightCategory
import com.martorell.albert.basketcoach.domain.model.InsightMetric
import com.martorell.albert.basketcoach.domain.model.InsightType
import com.martorell.albert.basketcoach.domain.model.MetricSentiment
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

    @Test
    fun `formatTopicContext formats insight into structured context block`() {
        val insight = Insight(
            id = "insight-turnovers",
            title = "Vulnerabilitat en pèrdues en primera línia",
            description = "18.4 pèrdues de pilota per partit han provocat 21.2 punts en contra.",
            category = InsightCategory.OFFENSE,
            relatedGameRounds = listOf(2, 4, 6),
            type = InsightType.IMPROVEMENT,
            contextTag = "Fase Ofensiva · Control de joc",
            metric = InsightMetric(
                primaryLabel = "Pèrdues per partit",
                primaryValue = "18.4",
                primarySubValue = "-4.2 vs mitjana",
                secondaryLabel = "Punts rebuts de pèrdua",
                secondaryValue = "21.2 pts/partit",
                secondaryTag = "dataset:to_pts",
                sentiment = MetricSentiment.NEGATIVE
            ),
            priority = "Alta",
            footerNote = "Afecta el balanç defensiu.",
            exploreChatPrompt = "Com reduir pèrdues en primera línia?"
        )

        val formatted = PromptTemplates.formatTopicContext(insight)

        assertTrue(formatted.contains("ACTIVE INTERVENTION AREA (THREAD FOCUS):"))
        assertTrue(formatted.contains("Title: Vulnerabilitat en pèrdues en primera línia"))
        assertTrue(formatted.contains("Priority / Type: Alta | IMPROVEMENT (Fase Ofensiva · Control de joc)"))
        assertTrue(formatted.contains("Pèrdues per partit: 18.4 (-4.2 vs mitjana)"))
        assertTrue(formatted.contains("Punts rebuts de pèrdua: 21.2 pts/partit"))
        assertTrue(formatted.contains("Rounds: 2, 4, 6"))
        assertTrue(formatted.contains("Note: Afecta el balanç defensiu."))
    }

    @Test
    fun `buildChatSystemPrompt with topicContext and localized label injects mandate and thread name`() {
        val topicContext = "ACTIVE INTERVENTION AREA: Rebounding Deficit"
        val prompt = PromptTemplates.buildChatSystemPrompt(
            gameContext = "Summary of 14 games",
            topicContext = topicContext,
            openInquiryLabel = "Consulta Libre",
            locale = Locale.forLanguageTag("es")
        )

        assertTrue(prompt.contains(topicContext))
        assertTrue(prompt.contains("STRICT THREAD FOCUS MANDATE"))
        assertTrue(prompt.contains("Respond ONLY with a short, polite redirect in the coach's language"))
        assertTrue(prompt.contains("Consulta Libre"))
        assertTrue(prompt.contains("GROUNDING CONTEXT (PHASE 1 MATCH DATA)"))
    }

    @Test
    fun `buildChatSystemPrompt injects open inquiry mandate when topicContext is null`() {
        val prompt = PromptTemplates.buildChatSystemPrompt(
            gameContext = "Summary of 14 games",
            topicContext = null,
            openInquiryLabel = "Consulta Lliure",
            locale = Locale.forLanguageTag("ca")
        )

        assertTrue(prompt.contains("THREAD BEHAVIOR MANDATE (OPEN INQUIRY / Consulta Lliure)"))
        assertTrue(prompt.contains("The coach is in the general consultation thread."))
    }
}
