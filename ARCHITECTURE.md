# BasketCoach Architecture

## 1. Overview & System Topology

BasketCoach is structured around a **Layer-Based Clean Architecture** topology across four primary modules:

```text
      +-----------------------------------+
      | :app (UI & Presentation)          |
      +-----------------------------------+
             |                     |
             v                     v
+-------------------------+ +----------------------------------+
| :usecases (Interactors) | | :data (Repositories & Sources)   |
+-------------------------+ +----------------------------------+
             |                     |
             |                     |
             +--------+   +--------+
                      v   v
      +-----------------------------------+
      | :domain (Entities & Contracts)    |
      +-----------------------------------+
```

### Module Responsibilities & Dependencies

| Module | Purpose | Allowed Dependencies | Platform Target |
| :--- | :--- | :--- | :--- |
| **`:domain`** | Core basketball domain entities, value objects, typed errors (`DomainError`), and repository contracts. | None (pure Kotlin JVM, zero external/Android dependencies). | Pure Kotlin JVM (`libs.plugins.kotlin.jvm`) |
| **`:usecases`** | Single-responsibility domain interactors orchestrating business workflows. | `:domain` | Pure Kotlin JVM / Android |
| **`:data`** | Repository implementations, local data parsing (`dataset.json`), database persistence (`Room`), and remote AI clients (`DevExpert API`). | `:domain` | Android / JVM |
| **`:app`** | Tablet Landscape Jetpack Compose UI, MVVM ViewModels with Hybrid UI State, and Hilt dependency injection root. | `:domain`, `:usecases`, `:data` | Android Application |

---

## 2. Domain Layer Governance (`:domain`)

The `:domain` module is the innermost layer and embodies the core business rules of basketball analytics:

- **Strict Pure Kotlin Mandate**:
  Zero Android framework dependencies (`android.*`). All types, collections, and primitives are pure Kotlin (`kotlin.*`).
- **Immutable State Representation**:
  Entities and value objects are modeled as immutable Kotlin `data class` types with `val` properties.
- **Single Source of Domain Truth**:
  Constants such as `OUR_TEAM_NAME = "BASQUETPIASABADELL BLAU"` reside directly in `:domain`, enabling dynamic resolution of home/away perspective, score differentials, and win/loss records.
- **Typed Functional Error Modeling**:
  Repository contracts return `Result<T, DomainError>` sealed hierarchies rather than throwing raw runtime exceptions, preventing technology leaks from data sources.

---

## 3. Core Domain Entities & Taxonomy

```text
[ Game ]
 ├─ round: Int
 ├─ date: String
 ├─ time: String
 ├─ isHomeGame: Boolean
 ├─ isWin: Boolean
 ├─ pointDifferential: Int
 │
 ├──> [ Score ] (homePoints, awayPoints)
 ├──> [ Venue ] (city, arena, address)
 └──> [ TeamStats ] (homeStats, awayStats)
       ├─ points, totalRebounds, assists, steals, blocks
       ├─ turnovers, personalFouls
       └─ fg2Pct, fg3Pct, ftPct, efgPct...

[ Insight ]
 ├─ id: String
 ├─ title: String
 ├─ description: String
 └─ category: InsightCategory

[ Drill ]
 ├─ id: String
 ├─ name: String
 ├─ objective: String
 └─ focusArea: String

[ ChatMessage ]
 ├─ id: String
 ├─ sender: MessageSender
 ├─ text: String
 └─ timestamp: Long
```

### Domain Repositories (Contracts in `:domain`)
- **`GameRepository`**: Access to historical Phase 1 game fixtures and team box scores (`getPhase1Games()`, `getGameByRound(round)`).
- **`AiAnalysisRepository`**: Querying tactical AI insights and recommended practice drills grounded in match context (`getInsights(gameContext)`, `getDrills(gameContext)`).
- **`ChatRepository`**: Single-thread conversational assistant interaction with the AI service (`getMessages()`, `sendMessage(messageText, gameContext)`).

---

## 4. Data Layer Governance & Local Fixture Pipeline (`:data`)

The `:data` module provides repository implementations and data sources while keeping `:domain` isolated from serialization annotations and transport schemas.

```text
[ dataset.json (Classpath Resource) ]
                 |
                 v (InputStream)
[ GameLocalDataSourceImpl (Dispatchers.IO) ]
                 |
                 v (List<GameDto>)
[ GameRepositoryImpl ]
                 |
                 v (safeCall + GameMapper.toDomain())
[ Result<List<Game>, DomainError> ]
                 |
                 v
[ :usecases Layer ]
```

### Key Architectural Patterns for Historical Games
1. **DTO Separation (`data.dto`)**:
   Raw snake_case JSON schemas are mapped into immutable `@Serializable` DTOs (`GameDto`, `ScoreDto`, `TeamStatsDto`). `:domain` remains completely clean of serialization dependencies.
2. **DTO-to-Domain Mapping (`data.mapper`)**:
   `GameMapper` converts DTOs to pure domain entities. It constructs domain value objects (`Venue`, `Score`) and injects points scored into `TeamStats.points` directly from the match final score.
3. **Bundled Classpath Resource Strategy (`GameLocalDataSource`)**:
   The static 14-game dataset is packaged at `data/src/main/resources/dataset.json`. `GameLocalDataSourceImpl` manages file streaming and JSON deserialization on `Dispatchers.IO`, throwing technical exceptions on failure.
4. **In-Memory Caching (SSOT)**:
   Because Phase 1 is a static 14-match historical dataset, `GameLocalDataSourceImpl` caches the parsed DTOs in a `@Volatile` field on the first query. Subsequent calls resolve synchronously without re-reading the stream.
5. **Safe Execution via `safeCall`**:
   `GameRepositoryImpl` coordinates data sources within `safeCall { ... }`. `FileNotFoundException` and `NoSuchElementException` map to `DomainError.Local.NotFound`, while serialization anomalies map to `DomainError.Generic`.

---

## 5. AI Inference & Conversational Assistant Pipeline (`:data`)

BasketCoach integrates with the DevExpert Inference API and provides seamless fallback to Google Gemini's OpenAI-compatible endpoint.

```text
       [ DevExpert / Gemini Inference API ]
                         ^
                         | (POST /v1/chat/completions)
                         v
       [ AiRemoteDataSourceImpl (OkHttp) ]
                         |
        +----------------+----------------+
        | (AiAnalysisResponseDto)         | (Completion Text)
        v                                 v
[ AiAnalysisRepositoryImpl ]      [ ChatRepositoryImpl ]
        ^                                 ^
        | (read/save)                     | (read/save)
        v                                 v
[ AiAnalysisLocalDataSource ]     [ ChatLocalDataSource ]
       (In-Memory SSOT)                  (In-Memory SSOT)
        |                                 |
        | (safeCall + toDomain)           | (safeCall)
        v                                 v
[ AiAnalysisRepository ]          [ ChatRepository ]
```

### Key Architectural Patterns for AI Integration
1. **OpenAI-Compatible Standard Endpoint**:
   Both DevExpert Inference (`https://inference.devexpert.io/v1`) and Google Gemini (`https://generativelanguage.googleapis.com/v1beta/openai`) adhere to the standard `POST /chat/completions` REST protocol with Bearer token authentication.
2. **Dual-Provider Configuration (`data.network.config`)**:
   - `AiClientConfig`: Pure data model holding `apiKey`, `baseUrl`, and `model`. `AiClientConfig.fromEnvironment()` checks `DEVEXPERT_API_KEY` first, falling back to `GEMINI_API_KEY`.
   - `DevExpertConfig`: Constants and defaults (`DEFAULT_BASE_URL`, models `"chat"` and `"chat-pro"`).
   - `GeminiConfig`: Constants and defaults (`DEFAULT_BASE_URL`, model `"gemini-2.5-flash"`).
   - `LocalPropertiesReader`: Reads local developer keys from root `local.properties` cleanly catching `IOException`.
3. **Data Source Separation (`data.source`)**:
   - `AiRemoteDataSource`: Executes OkHttp requests, strips markdown code fences (` ```json `), and deserializes strict DTOs. Blocking I/O is dispatched on `Dispatchers.IO`.
   - `AiAnalysisLocalDataSource`: Thread-safe in-memory cache protected by a `Mutex` so that a single AI analysis call populates both `Insight` and `Drill` caches simultaneously.
   - `ChatLocalDataSource`: Thread-safe single-thread message history storage protected by a `Mutex`.
4. **Mandatory DTO Schema & Context Grounding**:
   - `PromptTemplates.kt` grounds the AI in the 14 Phase 1 box scores (AST/TO, rebounding margins, personal foul averages, individual round scores) and FIBA rules.
   - All network DTO properties (`InsightDto`, `DrillDto`, `AiAnalysisResponseDto`, `ChatCompletionRequestDto`, `ChatCompletionResponseDto`) are strictly mandatory without defaults, and the system prompt explicitly forbids omitting keys or setting them to null.
5. **Exception Translation & Error Mapping (`data.util.ExceptionMapper`)**:
   - `Throwable.toDomainError()` maps timeouts to `DomainError.Network.Timeout`, 401 to `DomainError.Network.Unauthorized`, 500..599 to `DomainError.Network.ServerError`, and IO errors to `DomainError.Network.NoInternet`.
   - `safeCall` preserves structured concurrency by immediately rethrowing `CancellationException`.
