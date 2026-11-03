# AI Tactical Analysis & Structured Outputs Pattern

## 1. Executive Summary & Problem Definition

Large Language Models (LLMs) are inherently probabilistic sequence predictors. When asked for tactical basketball analysis, an LLM typically responds with conversational natural language, Markdown headers, bullet points, or unstructured prose.

However, a production-grade native Android UI (such as Jetpack Compose in `BasketCoach`) requires **deterministic, typed, and crash-resilient data models**. A UI cannot render conversational paragraphs into:
- 3-column tablet grid cards (`InsightCard`)
- Empirical comparison metrics (`InsightMetricPanel` with sentiment color tokens)
- Segmented tab filters (Tots / Fortaleses / Àrees de Millora)
- Interactive hand-off deep-links to the Socratic chat (`ChatRoute(initialPrompt)`)

To bridge this gap, BasketCoach implements the **Structured Outputs Pattern**. This pattern forces the remote inference engine (DevExpert / Google Gemini) to act not as a chatbot, but as a deterministic **JSON microservice** that strictly adheres to a predefined, strongly-typed Kotlin schema.

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
|    - stripMarkdownCodeFences(rawContent) (removes ```json ... ``` if emitted)                      |
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
|    GenerateInsightsUseCase -> Result<List<Insight>, DomainError>                                   |
|    AnalysisViewModel -> Passive load via LaunchedEffect(Unit)                                      |
|    AnalysisUiMapper -> Transforms List<Insight> into AnalysisUiContent.Success                     |
|    AnalysisScreen (Compose) -> Renders 3-Column Tablet Landscape Grid                             |
+----------------------------------------------------------------------------------------------------+
```

---

## 3. Detailed Component Breakdown

### 3.1 Grounding Context Generation (`PromptTemplates.formatGameSummary`)

LLMs are prone to hallucinating scores, rounds, or player statistics if not anchored in empirical evidence. BasketCoach enforces the **Zero-Assumption Rule** by synthesizing the 14 Phase 1 matches into a structured text prompt before querying the LLM:

```kotlin
// Condensed extract from PromptTemplates.kt
fun formatGameSummary(games: List<Game>): String {
    val totalGames = games.size
    val wins = games.count { it.isWin }
    val losses = totalGames - wins
    // Aggregate shooting %, AST/TO, rebounding margins, fouls
    // ...
    // Append round-by-round box scores
    for (game in games.sortedBy { it.round }) {
        summaryBuilder.appendLine(
            "Round ${game.round} (${if (game.isHomeGame) "HOME" else "AWAY"}, ${if (game.isWin) "WIN" else "LOSS"}): " +
            "$OUR_TEAM_NAME ${game.teamScore} - ${game.opponentScore} ${game.opponentName} | " +
            "AST: ${game.teamStats.assists}, TO: ${game.teamStats.turnovers} (AST/TO: ${game.teamStats.astToRatio}), " +
            "REB: ${game.teamStats.totalRebounds} vs ${game.opponentStats.totalRebounds}, " +
            "FOULS: ${game.teamStats.personalFouls}, 2P%: ${game.teamStats.fg2Pct}%, 3P%: ${game.teamStats.fg3Pct}%"
        )
    }
    return summaryBuilder.toString()
}
```

### 3.2 The System Prompt Schema Contract

In `PromptTemplates.ANALYSIS_SYSTEM_PROMPT`, the system prompt explicitly describes the target schema, mandatory field types, and allowed enum values:

```json
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
```

Key rules enforced in the prompt:
1. **Mandatory Non-Nullable Properties**: Null values or missing keys are explicitly prohibited.
2. **Deterministic Enums**: `category` (`OFFENSE`, `DEFENSE`, `SYNERGY`, `GENERAL`), `type` (`STRENGTH`, `IMPROVEMENT`), and `sentiment` (`POSITIVE`, `NEGATIVE`, `NEUTRAL`) map 1:1 to Kotlin domain enums.
3. **No Conversational Filler**: The model is instructed to output strictly valid JSON with zero conversational preamble (no "Sure, here is your analysis:").

### 3.3 Low-Entropy Inference & Markdown Stripping

To guarantee maximum schema compliance, inference is executed with **`temperature = 0.2`** in `AiRemoteDataSourceImpl`:

```kotlin
val requestDto = ChatCompletionRequestDto(
    model = config.model,
    messages = messages,
    temperature = 0.2
)
```

Because some LLMs wrap JSON responses in Markdown fences despite instructions, `AiRemoteDataSourceImpl` sanitizes the raw output before parsing:

```kotlin
private fun stripMarkdownCodeFences(rawContent: String): String {
    val trimmed = rawContent.trim()
    return if (trimmed.startsWith(CODE_FENCE)) {
        val lines = trimmed.lines()
        val startIndex = if (lines.first().startsWith(CODE_FENCE)) 1 else 0
        val endIndex = if (lines.last().trim() == CODE_FENCE) lines.size - 1 else lines.size
        lines.subList(startIndex, endIndex).joinToString("\n").trim()
    } else {
        trimmed
    }
}
```

### 3.4 Deserialization & Typed Domain Mapping

The sanitized string is parsed by `kotlinx.serialization`:

```kotlin
val sanitizedJson = stripMarkdownCodeFences(rawContent)
val responseDto = json.decodeFromString<AiAnalysisResponseDto>(sanitizedJson)
```

The DTO (`AiAnalysisResponseDto`) contains zero business logic. It is converted to domain entities via `AiAnalysisMapper.kt`:

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
```

### 3.5 Thread-Safe In-Memory Caching (SSOT)

Because Phase 1 is a static 14-game dataset, generating analysis once per session is sufficient:
- `AiAnalysisLocalDataSourceImpl` caches `List<Insight>` and `List<Drill>` behind a `kotlinx.coroutines.sync.Mutex`.
- When `AiAnalysisRepositoryImpl.getInsights()` is called, it first checks the cache.
- If the cache is empty, it invokes the remote AI API, converts DTOs to domain entities, populates the local cache, and returns the result. Subsequent calls return instantly without network overhead.

---

## 4. Error Handling & Resilience

The entire pipeline is protected by typed error mapping (`DomainError`):

| Technical Failure | Cause | DomainError Mapping | UI Handling |
| :--- | :--- | :--- | :--- |
| `java.net.SocketTimeoutException` | Network or LLM inference latency > 60s | `DomainError.Network.Timeout` | Shows retry banner with message |
| `HttpException(401)` | Invalid / missing `DEVEXPERT_API_KEY` | `DomainError.Network.Unauthorized` | Warns about API key configuration |
| `HttpException(500..599)` | DevExpert / Gemini upstream outage | `DomainError.Network.ServerError` | Asks coach to retry later |
| `SerializationException` | Model output violated JSON schema | `DomainError.Generic` | Safe fallback without crashing app |
| `IOException` / No route | Device in Airplane Mode / Offline | `DomainError.Network.NoInternet` | Offline indicator with manual refresh |

---

## 5. Summary of Benefits

1. **Type Safety & Zero UI Crashes**: The Compose UI binds to strongly typed Kotlin data classes (`Insight`, `InsightMetric`, `InsightCardUiModel`), never to raw JSON or dynamic dictionaries.
2. **Grounding & Zero Hallucinations**: Prompt grounding ensures the AI bases every conclusion on actual box score numbers from the 14 Phase 1 matches.
3. **Decoupled Architecture**: Clean Architecture boundaries ensure the UI knows nothing about OkHttp, JSON, or DevExpert endpoints; it simply receives `Result.Success(List<Insight>)` or `Result.Error(DomainError)`.
4. **Performance & Cost Optimization**: In-memory caching prevents redundant API invocations on screen re-navigation, keeping API latency and token costs minimal.
