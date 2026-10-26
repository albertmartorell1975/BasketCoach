package com.martorell.albert.basketcoach.data.network.prompt

import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.OUR_TEAM_NAME
import java.util.Locale

object PromptTemplates {

    val ANALYSIS_SYSTEM_PROMPT: String = """
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
        4. OUTPUT FORMAT: Respond ONLY with a valid JSON object. Every field shown in the schema below is strictly mandatory (do NOT omit any field, do NOT use null values). Match this exact schema without any conversational preamble or markdown code blocks:
        {
          "insights": [
            {
              "id": "insight-1",
              "title": "Concise headline",
              "description": "Tactical explanation and data evidence",
              "category": "OFFENSE" | "DEFENSE" | "SYNERGY" | "GENERAL",
              "relatedGameRounds": [1, 5, 8],
              "type": "STRENGTH" | "IMPROVEMENT",
              "contextTag": "e.g. Fase Ofensiva · Pintura",
              "metric": {
                "label": "Metric name",
                "value": "58.4%",
                "unit": "%",
                "deltaText": "+8.2% sobre mitjana",
                "sentiment": "POSITIVE",
                "evidenceLabel": "Evidència Digital",
                "evidenceValue": "14 Partits",
                "datasetKey": "dataset:paint_eff"
              },
              "footerNote": "Summary observation",
              "priority": "Crítica #1",
              "exploreChatPrompt": "Suggested query to explore this area in chat"
            }
          ],
          "drills": [
            {
              "id": "drill-1",
              "name": "Drill Title",
              "objective": "Targeted tactical improvement goal",
              "instructions": ["Step 1", "Step 2", "Step 3"],
              "focusArea": "e.g. Ball Protection & Transition Defense"
            }
          ]
        }
    """.trimIndent()

    val CHAT_SYSTEM_PROMPT: String = """
        You are the AI assistant coach for "$OUR_TEAM_NAME" in the Junior Men's Interterritorial basketball league under FIBA rules.
        You assist the head coach with tactical advice, game preparation, drill recommendations, and performance analysis.
        Be concise, professional, insightful, and basketball-grounded. Always reply in the same language the coach speaks to you.
    """.trimIndent()

    fun buildChatSystemPrompt(gameContext: String?): String {
        return if (!gameContext.isNullOrBlank()) {
            "$CHAT_SYSTEM_PROMPT\n\nGROUNDING CONTEXT (PHASE 1 MATCH DATA):\n$gameContext"
        } else {
            CHAT_SYSTEM_PROMPT
        }
    }

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

    fun buildAnalysisUserPrompt(gameSummary: String): String {
        return """
            Here is the complete Phase 1 match data for $OUR_TEAM_NAME:
            
            $gameSummary
            
            Please analyze these results and generate:
            1. Key tactical insights (strengths, vulnerabilities, and critical patterns).
            2. Tactical practice drills for Phase 2 to fix our turnover rates, rebounding, and defensive fouls.
            
            Remember: Return ONLY valid JSON matching the requested schema.
        """.trimIndent()
    }
}
