package com.martorell.albert.basketcoach.data.network.prompt

import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.OUR_TEAM_NAME
import java.util.Locale

object PromptTemplates {

    const val JEV_QUESTION_DOMAIN_RELEVANCE = "domain_relevance"
    const val JEV_CHOICE_BASKETBALL_RELEVANT = "basketball_relevant"
    const val JEV_CHOICE_OFF_TOPIC = "off_topic"
    const val JEV_QUESTION_TYPE_CHOICE = "choice"
    const val JEV_STATE_APP_DOMAIN = "app_domain"
    const val JEV_STATE_LAST_ASSISTANT_MESSAGE = "last_assistant_message"
    const val JEV_STATE_COACH_MESSAGE = "coach_message"
    const val JEV_APP_DOMAIN_VALUE = "Junior Men's Interterritorial basketball coaching, game analysis, tactical practice drills, and FIBA basketball strategy for $OUR_TEAM_NAME."
    const val JEV_QUESTION_TEXT = "Determine whether 'coach_message' is relevant to basketball tactics, game analysis, drills, FIBA rules, team management, or is a valid follow-up to the conversation."

    fun buildOffTopicRejectionMessage(locale: Locale = Locale.getDefault()): String {
        return when (locale.language.lowercase(Locale.ROOT)) {
            "es" -> "Como asistente táctico del Bàsquet Pia Sabadell, solo puedo responder a consultas sobre baloncesto, análisis de partidos, táctica y ejercicios de entrenamiento. ¿En qué puedo ayudar a tu equipo?"
            else -> "Com a assistent tàctic del Bàsquet Pia Sabadell, només puc respondre a consultes sobre bàsquet, anàlisi de partits, tàctica i exercicis d'entrenament. En què puc ajudar el teu equip?"
        }
    }

    /**
     * Resolves the target natural language for AI generation based on the active [Locale].
     *
     * Defaults to Catalan (the primary language of Basquet Pia Sabadell in the FCBQ),
     * and switches to Spanish when the system locale starts with "es".
     */
    fun resolveTargetLanguage(locale: Locale = Locale.getDefault()): String {
        return when (locale.language.lowercase(Locale.ROOT)) {
            "es" -> "Spanish (Español)"
            "ca" -> "Catalan (Català)"
            else -> "Catalan (Català)" // Base language of Basquet Pia Sabadell
        }
    }

    /**
     * System prompt establishing tactical persona, Phase 1 analytical rules, and strict JSON schema contract.
     * Evaluates dynamically using [Locale.getDefault].
     */
    val ANALYSIS_SYSTEM_PROMPT: String
        get() = buildAnalysisSystemPrompt()

    /**
     * Builds the tactical analysis system prompt parameterized by [locale].
     */
    fun buildAnalysisSystemPrompt(locale: Locale = Locale.getDefault()): String {
        val targetLanguage = resolveTargetLanguage(locale)
        return """
            You are an elite tactical assistant for the basketball head coach of "$OUR_TEAM_NAME", competing in the Junior Men's Interterritorial category under FIBA rules.
            Your mission is to perform an in-depth diagnosis of Phase 1 games to prepare the squad for Phase 2.
            
            CRITICAL RULES:
            1. Base your diagnosis strictly on the provided Phase 1 match box scores and metrics.
            2. Analyze key patterns:
               - Record: 3 wins, 11 losses.
               - Assist-to-turnover ratio (AST/TO): High turnover rates and low AST/TO (< 0.70) in losses vs solid ball security (>= 1.0) in wins.
               - Rebounding: Defensive rebounding vulnerability and deficit vs opponents.
               - Personal fouls: Foul trouble (averaging > 20 fouls in losses) leading to opponent free throw runs.
            3. Formulate actionable tactical insights and practice drills for Phase 2 to eliminate Phase 1 weaknesses and reinforce winning formulas.
            4. LANGUAGE MANDATE: All human-readable text fields in the output JSON (title, description, contextTag, metric primaryLabel, metric primarySubValue, metric secondaryLabel, priority, footerNote, exploreChatPrompt, drill name, objective, instructions, and focusArea) MUST be written in $targetLanguage. Keep standard basketball acronyms (AST, TO, REB, 2P, 3P, TL, eFG%) recognizable.
            5. OUTPUT FORMAT: Respond ONLY with a valid JSON object. Every field shown in the schema below is strictly mandatory (do NOT omit any field, do NOT use null values). Match this exact schema without any conversational preamble or markdown code blocks:
            {
              "insights": [
                {
                  "id": "insight-1",
                  "title": "Concise headline in $targetLanguage",
                  "description": "Tactical explanation and data evidence in $targetLanguage",
                  "category": "OFFENSE" | "DEFENSE" | "SYNERGY" | "GENERAL",
                  "relatedGameRounds": [1, 5, 8],
                  "type": "STRENGTH" | "IMPROVEMENT",
                  "contextTag": "e.g. Fase Ofensiva · Pintura",
                  "metric": {
                    "primaryLabel": "Label in $targetLanguage",
                    "primaryValue": "58.4%",
                    "primarySubValue": "SubValue in $targetLanguage",
                    "secondaryLabel": "Secondary label in $targetLanguage",
                    "secondaryValue": "1.24 pts/pos",
                    "secondaryTag": "dataset:paint_eff",
                    "sentiment": "POSITIVE" | "NEGATIVE" | "NEUTRAL"
                  },
                  "priority": "Alta | Mitjana | Baixa",
                  "footerNote": "Explanatory footer in $targetLanguage.",
                  "exploreChatPrompt": "Prompt to explore in chat in $targetLanguage"
                }
              ],
              "drills": [
                {
                  "id": "drill-1",
                  "name": "Drill Title in $targetLanguage",
                  "objective": "Targeted tactical improvement goal in $targetLanguage",
                  "instructions": ["Step 1 in $targetLanguage", "Step 2 in $targetLanguage", "Step 3 in $targetLanguage"],
                  "focusArea": "Focus area in $targetLanguage"
                }
              ]
            }
        """.trimIndent()
    }

    /**
     * Base system prompt for conversational chat assistance.
     * Evaluates dynamically using [Locale.getDefault].
     */
    val CHAT_SYSTEM_PROMPT: String
        get() = buildChatSystemPromptBase()

    /**
     * Builds the base conversational chat system prompt parameterized by [locale].
     */
    fun buildChatSystemPromptBase(locale: Locale = Locale.getDefault()): String {
        val targetLanguage = resolveTargetLanguage(locale)
        return """
            You are the AI assistant coach for "$OUR_TEAM_NAME" in the Junior Men's Interterritorial basketball league under FIBA rules.
            You assist the head coach with tactical advice, game preparation, drill recommendations, and performance analysis.
            Be concise, professional, insightful, and basketball-grounded. Always reply in $targetLanguage (or match the coach's language if they explicitly write in another language).
            
            STRICT DOMAIN GUARDRAIL: You are exclusively an AI tactical basketball coaching assistant for $OUR_TEAM_NAME. If the coach asks about topics completely unrelated to basketball, match analysis, coaching, drills, or team management (such as weather, politics, international conflicts, general trivia), politely decline and redirect the focus back to basketball.
        """.trimIndent()
    }

    /**
     * Formats an active tactical [insight] into structured thread grounding context.
     */
    fun formatTopicContext(insight: Insight): String {
        val metricDetails = listOfNotNull(
            "${insight.metric.primaryLabel}: ${insight.metric.primaryValue}${if (insight.metric.primarySubValue.isNotBlank()) " (${insight.metric.primarySubValue})" else ""}"
                .takeIf { insight.metric.primaryLabel.isNotBlank() || insight.metric.primaryValue.isNotBlank() },
            "${insight.metric.secondaryLabel}: ${insight.metric.secondaryValue}"
                .takeIf { insight.metric.secondaryLabel.isNotBlank() || insight.metric.secondaryValue.isNotBlank() }
        ).joinToString(" | ")

        val roundsStr = if (insight.relatedGameRounds.isNotEmpty()) {
            "Rounds: ${insight.relatedGameRounds.joinToString(", ")}"
        } else ""

        return buildString {
            appendLine("ACTIVE INTERVENTION AREA (THREAD FOCUS):")
            appendLine("- Title: ${insight.title}")
            appendLine("- Priority / Type: ${insight.priority} | ${insight.type.name} (${insight.contextTag})")
            appendLine("- Diagnosis: ${insight.description}")
            if (metricDetails.isNotBlank()) {
                appendLine("- Empirical Metrics: $metricDetails")
            }
            if (roundsStr.isNotBlank()) {
                appendLine("- Evidence: $roundsStr")
            }
            if (insight.footerNote.isNotBlank()) {
                appendLine("- Note: ${insight.footerNote}")
            }
        }.trimEnd()
    }

    /**
     * Constructs the full chat system prompt incorporating optional match grounding context and [locale].
     */
    fun buildChatSystemPrompt(
        gameContext: String?,
        locale: Locale = Locale.getDefault()
    ): String = buildChatSystemPrompt(gameContext = gameContext, topicContext = null, locale = locale)

    /**
     * Constructs the full chat system prompt incorporating optional match grounding context,
     * focused improvement area context, and [locale].
     */
    fun buildChatSystemPrompt(
        gameContext: String?,
        topicContext: String?,
        locale: Locale = Locale.getDefault()
    ): String {
        val basePrompt = buildChatSystemPromptBase(locale)
        val threadMandate = if (!topicContext.isNullOrBlank()) {
            """
                $topicContext
                
                STRICT THREAD FOCUS MANDATE:
                You are strictly confined to the active intervention area specified above.
                - This thread is NOT a general chat. General questions or other basketball topics belong exclusively in the "Consulta Lliure" thread.
                - When the coach asks about this active intervention area (its tactics, drills, metrics, root causes, practice progression): provide deep, tactical, data-grounded analysis and actionable drills.
                - If the coach asks about an aspect, skill, or tactical factor different from this active intervention area (such as outside shooting, 3-point percentages, defensive rebounding, zone defense, physical training, etc.):
                  1. DO NOT provide analysis, statistical breakdowns, drills, or advice on that other topic.
                  2. Clearly and politely inform the coach that this thread is exclusively dedicated to this active intervention area, and invite them to explore that other topic in the "Consulta Lliure" thread.
                  3. Redirect the conversation immediately back to the active intervention area with a concise, actionable question about resolving this specific weakness.
            """.trimIndent()
        } else {
            """
                THREAD BEHAVIOR MANDATE (OPEN INQUIRY / CONSULTA LLIURE):
                The coach is in the general consultation thread. You may freely address any basketball question, match analysis, player role, or practice drill across all 14 Phase 1 matches.
            """.trimIndent()
        }

        val grounding = if (!gameContext.isNullOrBlank()) {
            "\n\nGROUNDING CONTEXT (PHASE 1 MATCH DATA):\n$gameContext"
        } else {
            ""
        }

        return "$basePrompt\n\n$threadMandate$grounding"
    }

    /**
     * Formats the list of [games] into a concise, factual summary for grounding AI inference.
     */
    fun formatGameSummary(games: List<Game>): String {
        if (games.isEmpty()) return "No games recorded for Phase 1."

        val totalGames = games.size
        val wins = games.count { it.isWin }
        val losses = totalGames - wins
        val totalPointsScored = games.sumOf { it.teamScore }
        val totalPointsConceded = games.sumOf { it.opponentScore }
        val avgScored = totalPointsScored.toDouble() / totalGames
        val avgConceded = totalPointsConceded.toDouble() / totalGames

        val totalTurnovers = games.sumOf { it.teamStats.turnovers }
        val totalAssists = games.sumOf { it.teamStats.assists }
        val avgTurnovers = totalTurnovers.toDouble() / totalGames
        val avgAssists = totalAssists.toDouble() / totalGames
        val avgAstTo = if (totalTurnovers > 0) totalAssists.toDouble() / totalTurnovers else 0.0

        val totalRebounds = games.sumOf { it.teamStats.totalRebounds }
        val oppTotalRebounds = games.sumOf { it.opponentStats.totalRebounds }
        val avgRebounds = totalRebounds.toDouble() / totalGames
        val avgOppRebounds = oppTotalRebounds.toDouble() / totalGames

        val totalFouls = games.sumOf { it.teamStats.personalFouls }
        val avgFouls = totalFouls.toDouble() / totalGames

        val summaryBuilder = StringBuilder()
        summaryBuilder.appendLine("PHASE 1 SUMMARY FOR $OUR_TEAM_NAME:")
        summaryBuilder.appendLine("- Record: $wins Wins, $losses Losses in $totalGames games.")
        summaryBuilder.appendLine("- Scoring: Avg Scored: ${String.format(Locale.US, "%.1f", avgScored)} pts | Avg Conceded: ${String.format(Locale.US, "%.1f", avgConceded)} pts.")
        summaryBuilder.appendLine("- Ball Security: Avg Assists: ${String.format(Locale.US, "%.1f", avgAssists)} | Avg Turnovers: ${String.format(Locale.US, "%.1f", avgTurnovers)} | Overall AST/TO: ${String.format(Locale.US, "%.2f", avgAstTo)}.")
        summaryBuilder.appendLine("- Rebounding: Avg Team: ${String.format(Locale.US, "%.1f", avgRebounds)} | Avg Opponent: ${String.format(Locale.US, "%.1f", avgOppRebounds)} (Margin: ${String.format(Locale.US, "%+.1f", avgRebounds - avgOppRebounds)}).")
        summaryBuilder.appendLine("- Personal Fouls: Avg: ${String.format(Locale.US, "%.1f", avgFouls)} fouls per game.")
        summaryBuilder.appendLine()
        summaryBuilder.appendLine("INDIVIDUAL ROUND BREAKDOWN:")

        for (game in games.sortedBy { it.round }) {
            val resultStr = if (game.isWin) "WIN" else "LOSS"
            val venueStr = if (game.isHomeGame) "HOME" else "AWAY"
            summaryBuilder.appendLine(
                "Round ${game.round} ($venueStr, $resultStr): " +
                "$OUR_TEAM_NAME ${game.teamScore} - ${game.opponentScore} ${game.opponentName} | " +
                "AST: ${game.teamStats.assists}, TO: ${game.teamStats.turnovers} (AST/TO: ${String.format(Locale.US, "%.2f", game.teamStats.astToRatio)}), " +
                "REB: ${game.teamStats.totalRebounds} vs ${game.opponentStats.totalRebounds}, " +
                "FOULS: ${game.teamStats.personalFouls}, 2P%: ${String.format(Locale.US, "%.1f", game.teamStats.fg2Pct)}%, 3P%: ${String.format(Locale.US, "%.1f", game.teamStats.fg3Pct)}%"
            )
        }

        return summaryBuilder.toString()
    }

    /**
     * Builds the analysis user prompt containing grounded match data and [locale] instructions.
     */
    fun buildAnalysisUserPrompt(gameSummary: String, locale: Locale = Locale.getDefault()): String {
        val targetLanguage = resolveTargetLanguage(locale)
        return """
            Here is the complete Phase 1 match data for $OUR_TEAM_NAME:
            
            $gameSummary
            
            Please analyze these results and generate:
            1. Key tactical insights (strengths, vulnerabilities, and critical patterns).
            2. Tactical practice drills for Phase 2 to fix our turnover rates, rebounding, and defensive fouls.
            
            Remember: Return ONLY valid JSON matching the requested schema. All text fields must be written in $targetLanguage.
        """.trimIndent()
    }
}
