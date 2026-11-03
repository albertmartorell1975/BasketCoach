# AI Tactical Analysis & Structured Outputs Pattern

> Part of the core application architecture. See also: [ARCHITECTURE.md](../../ARCHITECTURE.md).

## 1. Executive Summary & Problem Definition

Large Language Models (LLMs) are inherently probabilistic sequence predictors. When asked for tactical basketball analysis, an LLM typically responds with conversational natural language, Markdown headers, bullet points, or unstructured prose.

However, a production-grade native Android UI (such as Jetpack Compose in `BasketCoach`) requires **deterministic, typed, and crash-resilient data models**. The UI renders:
- 3-column tablet grid cards (`InsightCard`)
- Empirical comparison metrics (`InsightMetricPanel` with sentiment color tokens)
- Segmented tab filters (`InsightFilterTab`: Tots / Fortaleses / Àrees de Millora)
- Interactive hand-off deep-links to the Socratic chat (`ChatRoute(initialPrompt)`)

To bridge this gap, BasketCoach implements the **Structured Outputs Pattern**. This pattern forces the remote inference engine (DevExpert / Google Gemini) to act not as an open-ended conversational bot, but as a deterministic **JSON microservice** adhering strictly to a predefined schema contract matching Kotlin DTOs.

---

## 2. End-to-End Pipeline Architecture

The tactical analysis pipeline operates across the Clean Architecture boundaries (`:data` -> `:domain` -> `:usecases` -> `:app`):

```text
+----------------------------------------------------------------------------------------------------+
| 1. CONTEXT GROUNDING                                                                               |
|    GameRepository -> List<Game> (14 Phase 1 fixtures)                                              |
|    PromptTemplates.formatGameSummary(games) -> Textual Box Score Baseline (3V-11D, AST/TO, REB)   |
+----------------------------------------------------------------------------------------------------+
                                                 |
                                                 v
+----------------------------------------------------------------------------------------------------+
| 2. SCHEMA PROMPT CONTRACT (Structured Outputs)                                                     |
|    PromptTemplates.ANALYSIS_SYSTEM_PROMPT:                                                         |
|    - FIBA Junior Interterritorial Tactical Persona                                                 |
|    - Strict Rules (Zero Assumption, empirical facts)                                               |
|    - Strict JSON Schema Specification (AiAnalysisResponseDto contract)                             |
|    - Explicit constraint: "Return ONLY valid JSON matching this schema, no markdown, no nulls"    |
+----------------------------------------------------------------------------------------------------+
                                                 |
                                                 v
+----------------------------------------------------------------------------------------------------+
| 3. REMOTE INFERENCE (DevExpert / Gemini API)                                                       |
|    POST /v1/chat/completions (OpenAI-compatible protocol)                                          |
|    - temperature = 0.2 (low entropy for maximum determinism and schema adherence)                  |
|    - Payload: system prompt + user grounding prompt                                                |
+----------------------------------------------------------------------------------------------------+
                                                 |
                                                 v
+----------------------------------------------------------------------------------------------------+
| 4. SANITIZATION & STRICT DESERIALIZATION                                                           |
|    AiRemoteDataSourceImpl:                                                                         |
|    - stripMarkdownCodeFences(raw) (safely strips ```json code fences if emitted)                  |
|    - kotlinx.serialization: json.decodeFromString<AiAnalysisResponseDto>(sanitizedJson)            |
+----------------------------------------------------------------------------------------------------+
                                                 |
                                                 v
+----------------------------------------------------------------------------------------------------+
| 5. DOMAIN MAPPING & LOCAL SSOT CACHING                                                             |
|    AiAnalysisMapper: DTOs -> Pure Kotlin Domain Entities (List<Insight>, List<Drill>)             |
|    AiAnalysisLocalDataSourceImpl: Thread-safe in-memory cache protected by Mutex                   |
+----------------------------------------------------------------------------------------------------+
                                                 |
                                                 v
+----------------------------------------------------------------------------------------------------+
| 6. USE CASE & PRESENTATION COORDINATION                                                            |
|    GetPhase1GamesUseCase -> Result<List<Game>, DomainError>                                        |
|    GenerateInsightsUseCase -> Result<List<Insight>, DomainError>                                   |
|    AnalysisViewModel -> Coordinates both interactors with passive lifecycle                        |
|    AnalysisUiMapper.toSuccess(games, allInsights, tab) -> AnalysisUiContent.Success                |
|    AnalysisScreen (Compose) -> Renders 3-Column Tablet Landscape Grid                             |
+----------------------------------------------------------------------------------------------------+
```

---

## 3. Detailed Component Breakdown

### 3.1 Grounding Context Generation (`PromptTemplates.formatGameSummary`)

LLMs are prone to hallucinating scores, rounds, or player statistics if not anchored in empirical evidence. BasketCoach enforces the **Zero-Assumption Rule** by synthesizing the 14 Phase 1 matches into a structured text prompt before querying the LLM.

Source: [`PromptTemplates.kt`](../../data/src/main/kotlin/com/martorell/albert/basketcoach/data/network/prompt/PromptTemplates.kt)

```kotlin
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
```

### 3.2 The System Prompt Schema Contract

In `PromptTemplates.ANALYSIS_SYSTEM_PROMPT`, the system prompt explicitly establishes the persona, tactical guidelines, and the mandatory JSON schema structure.

Source: [`PromptTemplates.kt`](../../data/src/main/kotlin/com/martorell/albert/basketcoach/data/network/prompt/PromptTemplates.kt)

```kotlin
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
            "primaryLabel": "Percentatge T2 Zona",
            "primaryValue": "58.4%",
            "primarySubValue": "+8.2% sobre mitjana lliga",
            "secondaryLabel": "Punts / Pos.",
            "secondaryValue": "1.24 pts/pos",
            "secondaryTag": "dataset:paint_eff",
            "sentiment": "POSITIVE" | "NEGATIVE" | "NEUTRAL"
          },
          "priority": "Alta",
          "footerNote": "Factor diferencial en victòries.",
          "exploreChatPrompt": "Quins exercicis d'entrenament podem aplicar per a la Fase 2?"
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
```

#### Example Valid JSON Returned by the API

The model produces a strictly valid JSON response that matches the schema:

```json
{
  "insights": [
    {
      "id": "insight-paint-eff",
      "title": "Eficiència Alta a la Pintura en Transició",
      "description": "En les 3 victòries l'equip ha assolit un 58.4% en tirs de 2 punts dins de la zona, aprofitant contraatacs directes.",
      "category": "OFFENSE",
      "relatedGameRounds": [3, 9, 10],
      "type": "STRENGTH",
      "contextTag": "Fase Ofensiva · Pintura",
      "metric": {
        "primaryLabel": "Percentatge T2 Zona",
        "primaryValue": "58.4%",
        "primarySubValue": "+8.2% sobre mitjana lliga",
        "secondaryLabel": "Punts / Pos.",
        "secondaryValue": "1.24 pts/pos",
        "secondaryTag": "dataset:paint_eff",
        "sentiment": "POSITIVE"
      },
      "priority": "Alta",
      "footerNote": "Factor clau en victòries.",
      "exploreChatPrompt": "Com podem mantenir aquest percentatge en pintura contra defenses tancades a la Fase 2?"
    }
  ],
  "drills": [
    {
      "id": "drill-box-out-transition",
      "name": "Tancament de Rebot Defensiu i Sortida Ràpida",
      "objective": "Assegurar el rebot defensiu per reduir els punts de segona oportunitat dels rivals.",
      "instructions": [
        "Situar 3 atacants exteriors i 3 defensors a la línia de tirs lliures.",
        "Llançament intencionat i contacte obligatori per tancar el rebot durant 2 segons.",
        "Primer passi immediat al base obert sense botar a camp defensiu."
      ],
      "focusArea": "Rebot Defensiu i Transició"
    }
  ]
}
```

Key rules enforced:
1. **Mandatory Non-Nullable Properties**: Null values or missing keys are explicitly prohibited.
2. **Deterministic Enums**: `category` (`OFFENSE`, `DEFENSE`, `SYNERGY`, `GENERAL`), `type` (`STRENGTH`, `IMPROVEMENT`), and `sentiment` (`POSITIVE`, `NEGATIVE`, `NEUTRAL`) map 1:1 to Kotlin domain enums.
3. **No Conversational Filler**: The model is instructed to output strictly valid JSON with zero conversational preamble.

### 3.3 Low-Entropy Inference & Markdown Code Fence Stripping

To guarantee maximum schema compliance, inference is executed with **`temperature = 0.2`** in `AiRemoteDataSourceImpl`:

Source: [`AiRemoteDataSourceImpl.kt`](../../data/src/main/kotlin/com/martorell/albert/basketcoach/data/source/remote/AiRemoteDataSourceImpl.kt)

```kotlin
override suspend fun generateAnalysis(gameContext: String): AiAnalysisResponseDto {
    val userPrompt = PromptTemplates.buildAnalysisUserPrompt(gameContext)
    val messages = listOf(
        ChatMessageDto(role = ChatRole.SYSTEM, content = PromptTemplates.ANALYSIS_SYSTEM_PROMPT),
        ChatMessageDto(role = ChatRole.USER, content = userPrompt)
    )
    val requestDto = ChatCompletionRequestDto(
        model = config.model,
        messages = messages,
        temperature = 0.2
    )

    val rawContent = executeChatCompletion(requestDto)
    val sanitizedJson = stripMarkdownCodeFences(rawContent)
    return json.decodeFromString<AiAnalysisResponseDto>(sanitizedJson)
}
```

Because some LLM providers wrap JSON in Markdown code fences (` ```json ... ``` `), `AiRemoteDataSourceImpl` strips them safely:

```kotlin
private fun stripMarkdownCodeFences(raw: String): String {
    val trimmed = raw.trim()
    if (trimmed.startsWith(CODE_FENCE)) {
        val lines = trimmed.lines()
        val withoutFirst = if (lines.isNotEmpty() && lines.first().startsWith(CODE_FENCE)) {
            lines.drop(1)
        } else {
            lines
        }
        val withoutLast = if (withoutFirst.isNotEmpty() && withoutFirst.last().startsWith(CODE_FENCE)) {
            withoutLast.dropLast(1)
        } else {
            withoutLast
        }
        return withoutLast.joinToString("\n").trim()
    }
    return trimmed
}
```

### 3.4 Deserialization & Typed Domain Mapping

The sanitized string is parsed into strongly-typed DTOs by `kotlinx.serialization`:

Sources:
- [`AiAnalysisResponseDto.kt`](../../data/src/main/kotlin/com/martorell/albert/basketcoach/data/network/dto/AiAnalysisResponseDto.kt)
- [`InsightMetricDto.kt`](../../data/src/main/kotlin/com/martorell/albert/basketcoach/data/network/dto/InsightMetricDto.kt)
- [`AiAnalysisMapper.kt`](../../data/src/main/kotlin/com/martorell/albert/basketcoach/data/mapper/AiAnalysisMapper.kt)

```kotlin
fun InsightDto.toDomain(defaultId: String = UUID.randomUUID().toString()): Insight {
    return Insight(
        id = id.ifBlank { defaultId },
        title = title,
        description = description,
        category = InsightCategory.fromString(category),
        relatedGameRounds = relatedGameRounds,
        type = InsightType.fromString(type),
        contextTag = contextTag,
        metric = metric.toDomain(),
        priority = priority,
        footerNote = footerNote,
        exploreChatPrompt = exploreChatPrompt
    )
}

fun InsightMetricDto.toDomain(): InsightMetric {
    return InsightMetric(
        primaryLabel = primaryLabel,
        primaryValue = primaryValue,
        primarySubValue = primarySubValue,
        secondaryLabel = secondaryLabel,
        secondaryValue = secondaryValue,
        secondaryTag = secondaryTag,
        sentiment = MetricSentiment.fromString(sentiment)
    )
}

fun DrillDto.toDomain(defaultId: String = UUID.randomUUID().toString()): Drill {
    return Drill(
        id = id.ifBlank { defaultId },
        name = name,
        objective = objective,
        instructions = instructions,
        focusArea = focusArea
    )
}
```

### 3.5 Thread-Safe In-Memory Caching (SSOT)

Because Phase 1 is a static 14-game dataset, generating analysis once per session is sufficient:
- `AiAnalysisLocalDataSourceImpl` caches `List<Insight>` and `List<Drill>` behind a `kotlinx.coroutines.sync.Mutex`.
- When `AiAnalysisRepositoryImpl.getInsights()` is called, it first checks the cache.
- If the cache is empty, it invokes the remote AI API, converts DTOs to domain entities, populates the local cache, and returns the result. Subsequent calls return instantly without network overhead.

Source: [`AiAnalysisRepositoryImpl.kt`](../../data/src/main/kotlin/com/martorell/albert/basketcoach/data/repository/AiAnalysisRepositoryImpl.kt)

```kotlin
override suspend fun getInsights(games: List<Game>): Result<List<Insight>, DomainError> = safeCall {
    localDataSource.getCachedInsights() ?: run {
        fetchAndCacheAnalysis(games)
        localDataSource.getCachedInsights().orEmpty()
    }
}
```

---

## 4. Error Handling & Resilience

The entire pipeline is protected by typed error mapping (`DomainError`), converting technical failures into user-friendly localized messages without leaking Android UI dependencies into the data or use case layers:

Sources:
- [`ExceptionMapper.kt`](../../data/src/main/kotlin/com/martorell/albert/basketcoach/data/mapper/ExceptionMapper.kt)
- [`AnalysisErrorExt.kt`](../../app/src/main/java/com/martorell/albert/basketcoach/ui/analysis/mapper/AnalysisErrorExt.kt)

| Technical Failure | Cause | DomainError Mapping | UI Handling (`toAnalysisUiText()`) |
| :--- | :--- | :--- | :--- |
| `SocketTimeoutException` | Network or LLM inference latency > 60s | `DomainError.Network.Timeout` | `R.string.analysis_error_timeout` |
| `HttpException(401)` | Invalid / missing `DEVEXPERT_API_KEY` | `DomainError.Network.Unauthorized` | `R.string.analysis_error_unauthorized` |
| `HttpException(500..599)` | DevExpert / Gemini upstream outage | `DomainError.Network.ServerError(code)` | Server error message / `R.string.analysis_error_generic` |
| `SerializationException` | Model output violated JSON schema | `DomainError.Generic` | Generic error message / `R.string.analysis_error_generic` |
| `IOException` / No route | Device in Airplane Mode / Offline | `DomainError.Network.NoInternet` | `R.string.analysis_error_no_internet` |

---

## 5. Summary of Architectural Benefits

1. **Type Safety & Zero UI Crashes**: The Compose UI binds to strongly typed Kotlin data classes (`Insight`, `InsightMetric`, `InsightCardUiModel`), never to raw JSON or dynamic dictionaries.
2. **Grounding & Zero Hallucinations**: Prompt grounding ensures the AI bases every conclusion on actual box score numbers from the 14 Phase 1 matches.
3. **Decoupled Architecture**: Clean Architecture boundaries ensure the UI knows nothing about OkHttp, JSON, or DevExpert endpoints; it simply receives `Result.Success(List<Insight>)` or `Result.Error(DomainError)`.
4. **Performance & Cost Optimization**: In-memory caching prevents redundant API invocations on screen re-navigation, keeping API latency and token costs minimal.
