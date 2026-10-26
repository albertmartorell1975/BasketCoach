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
| **`:usecases`** | Single-responsibility domain interactors orchestrating business workflows. | `:domain` (pure Kotlin JVM, zero Android/data dependencies). | Pure Kotlin JVM (`libs.plugins.kotlin.jvm`) |
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

## 3. Use Cases Layer Governance (`:usecases`)

The `:usecases` module encapsulates focused, single-responsibility domain interactors (business logic) orchestrating workflows across repositories.

- **Strict Pure Kotlin JVM Isolation**:
  The module depends exclusively on `:domain` and testing libraries (`junit`, `kotlinx-coroutines-test`). It contains zero Android framework dependencies (`android.*`) and zero dependencies on `:data`.
- **Single-Responsibility Interactor Convention**:
  Each use case exposes a single public `suspend operator fun invoke(...)` function, returning functional `Result<T, DomainError>` sealed hierarchies.
- **Cross-Aggregate Orchestration & Context Grounding**:
  Per Clean Architecture and project constraints (Constraint #11), repositories remain completely decoupled and never inject or depend on one another. Any cross-aggregate coordination or contextual enrichment is orchestrated by Use Cases in this layer:
  - `GetPhase1GamesUseCase`: Retrieves all 14 Phase 1 fixtures directly from `GameRepository`.
  - `GetGameByRoundUseCase`: Queries a specific fixture by competition round (1..14), returning `DomainError.Local.NotFound` if missing.
  - `GenerateInsightsUseCase`: Retrieves matches from `GameRepository` and supplies them as grounding context to `AiAnalysisRepository.getInsights(games)`. Propagates errors immediately without calling AI services if game loading fails.
  - `GetPracticeDrillsUseCase`: Retrieves matches from `GameRepository` and supplies them to `AiAnalysisRepository.getDrills(games)`. Propagates errors immediately without calling AI services if game loading fails.
  - `GetChatMessagesUseCase`: Retrieves historical chat messages from `ChatRepository.getMessages()`.
  - `SendChatMessageUseCase`: Enriches chat interactions by fetching match history from `GameRepository` and passing it to `ChatRepository.sendMessage(messageText, games)`. Validates against blank inputs (`DomainError.Generic`), and gracefully falls back to passing `games = null` if game retrieval fails so that conversational chat remains operational.

---

## 4. Core Domain Entities & Taxonomy

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
 ├─ category: InsightCategory
 └─ relatedGameRounds: List<Int>

[ Drill ]
 ├─ id: String
 ├─ name: String
 ├─ objective: String
 ├─ instructions: List<String>
 └─ focusArea: String

[ ChatMessage ]
 ├─ id: String
 ├─ sender: MessageSender
 ├─ text: String
 └─ timestamp: Long
```

### Domain Repositories (Contracts in `:domain`)
- **`GameRepository`**: Access to historical Phase 1 game fixtures and team box scores (`getPhase1Games()`, `getGameByRound(round)`).
- **`AiAnalysisRepository`**: Querying tactical AI insights and recommended practice drills grounded in match context (`getInsights(games)`, `getDrills(games)`).
- **`ChatRepository`**: Single-thread conversational assistant interaction with the AI service (`getMessages()`, `sendMessage(messageText, games)`).

---

## 5. Data Layer Governance & Local Fixture Pipeline (`:data`)

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

## 6. AI Inference & Conversational Assistant Pipeline (`:data`)

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
   
---

## 7. UI & Presentation Layer Governance (`:app`)

The `:app` module delivers a specialized **Tablet Landscape** interface optimized for basketball coaches reviewing historical game data and tactical insights.

```text
[ MainActivity (sensorLandscape) ]
              │
              ▼
[ Navigation (NavHost + Destinations) ] ── (BasketCoachAppState)
              │
              ▼
    [ DashboardScreen ] (Stateful Screen - State Management)
              │
              ▼
    [ DashboardContent ] (Stateless Content - Scaffold & TopAppBar)
        ├── Loading State (CircularProgressIndicator)
        ├── Error State (Error Banner + Retry)
        └── Success State (Two-Pane Horizontal Layout)
                 ├── Left Pane (42%): [ GameListPane ]
                 │         ├── TeamRecordCard (Wins/Losses summary)
                 │         ├── SelectedGameDetailCard (Detailed box score)
                 │         └── LazyColumn of [ GameCard ] (14 Phase 1 fixtures)
                 │
                 ├── [ VerticalDivider ]
                 │
                 └── Right Pane (58%): [ InsightsDrillsPane ]
                           ├── PrimaryTabRow (All / Insights / Drills)
                           └── LazyColumn
                                 ├── [ InsightCard ] (DEFENSE, OFFENSE, SYNERGY, GENERAL)
                                 └── [ DrillCard ] (Focus area, objective, step execution)
```

### Key UI Architectural Patterns

1. **Navigation Structure (`navigation`)**:
   - `Destination`: Type-safe `@Serializable` sealed interface defining root destinations (`Dashboard`, `Chat`).
   - `BasketCoachAppState`: Hoisted state holder managing `NavHostController`, backstack destination observation, and global snackbar dispatch via `SnackbarHostState`.
   - `Navigation`: Root composable graph encapsulating `NavHost` with `Destination.Dashboard` as `startDestination` and bottom-anchored `SnackbarHost`.

2. **Stateful Screen / Stateless Content Separation**:
   - **Stateful Screen (`DashboardScreen`)**: Collects UI state, binds high-level screen interactions, and hoists navigation callbacks (`onNavigateToChat`). Prepared for ViewModel integration in `feature_007_dashboard_integration`.
   - **Stateless Content (`DashboardContent`)**: Pure composable receiving immutable `DashboardUiState` and emitting hoisted lambda callbacks (`onGameSelected`, `onTabSelected`, `onNavigateToChat`, `onRefresh`). Enables fast, deterministic Compose UI testing and multi-preview rendering.

3. **Tablet Landscape Two-Pane Layout**:
   - Locked to `android:screenOrientation="sensorLandscape"` in `AndroidManifest.xml` (tablet landscape target `w1280dp-h800dp`).
   - Horizontal split separated by a subtle `VerticalDivider`:
     - **`GameListPane` (~42% weight)**: Team summary record card (3W-11L, scoring and conceding averages), expandable/inspector `SelectedGameDetailCard` (rebounds, AST/TO, shooting splits, fouls), and scrollable list of 14 Phase 1 matches with W/L status badges.
     - **`InsightsDrillsPane` (~58% weight)**: Dynamic tab row filtering between all recommendations, AI tactical insights categorized with color-coded chips, and structured practice drills with step-by-step instructions.

4. **Design Tokens & Visual Identity (`ui/theme`)**:
   - Club Esportiu Pia Sabadell color scheme (`PiaPrimaryBlue`, `PiaNavy`, basketball accent `PiaAmber`, `WinGreen`, `LossRed`).
   - Multi-preview annotations (`@TabletLandscapePreview`, `@ComponentPreview`) providing light, dark, and 1.5x font scale accessibility verification directly in Android Studio.

