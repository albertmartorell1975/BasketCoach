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
  - `GetChatMessagesUseCase`: Retrieves historical chat messages from `ChatRepository.getMessages(topicId)`.
  - `SendChatMessageUseCase`: Enriches chat interactions by fetching match history from `GameRepository` and passing it to `ChatRepository.sendMessage(messageText, games, topicId)`. Validates against blank inputs (`DomainError.Generic`), and gracefully falls back to passing `games = null` if game retrieval fails so that conversational chat remains operational.

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
- **`ChatRepository`**: Topic-scoped conversational assistant interaction with the AI service (`getMessages(topicId)`, `sendMessage(messageText, games, topicId)`).

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
   - `GeminiConfig`: Constants and defaults (`DEFAULT_BASE_URL`, model `"gemini-3.5-flash"`).
   - `LocalPropertiesReader`: Reads local developer keys from root `local.properties` cleanly catching `IOException`.
3. **Data Source Separation (`data.source`)**:
   - `AiRemoteDataSource`: Executes OkHttp requests, strips markdown code fences (` ```json `), and deserializes strict DTOs. Blocking I/O is dispatched on `Dispatchers.IO`.
   - `AiAnalysisLocalDataSource`: Thread-safe in-memory cache protected by a `Mutex` so that a single AI analysis call populates both `Insight` and `Drill` caches simultaneously.
   - `ChatLocalDataSource`: Thread-safe topic-partitioned message history storage protected by a `Mutex`.
4. **Mandatory DTO Schema & Context Grounding**:
   - `PromptTemplates.kt` grounds the AI in the 14 Phase 1 box scores (AST/TO, rebounding margins, personal foul averages, individual round scores) and FIBA rules.
   - All network DTO properties (`InsightDto`, `DrillDto`, `AiAnalysisResponseDto`, `ChatCompletionRequestDto`, `ChatCompletionResponseDto`) are strictly mandatory without defaults, and the system prompt explicitly forbids omitting keys or setting them to null.
5. **Exception Translation & Error Mapping (`data.util.ExceptionMapper`)**:
   - `Throwable.toDomainError()` maps timeouts to `DomainError.Network.Timeout`, 401 to `DomainError.Network.Unauthorized`, 500..599 to `DomainError.Network.ServerError`, and IO errors to `DomainError.Network.NoInternet`.
   - `safeCall` preserves structured concurrency by immediately rethrowing `CancellationException`.

<a id="section-61-structured-outputs"></a>
### 6.1 Structured Outputs Pattern & AI Tactical Grounding Pipeline

BasketCoach utilizes the **Structured Outputs Pattern** to bridge non-deterministic LLM text generation with deterministic Jetpack Compose UI requirements:

1. **Context Grounding (`PromptTemplates.formatGameSummary`)**: Before invoking the AI, all 14 Phase 1 games are aggregated into factual box score baselines (AST/TO ratios, rebounding differentials, personal fouls, round-by-round margins).
2. **Schema Contract (`PromptTemplates.ANALYSIS_SYSTEM_PROMPT`)**: The system prompt enforces a strict JSON schema contract mirroring `AiAnalysisResponseDto`. It forbids markdown commentary, missing fields, or null values.
3. **Low-Entropy Remote Inference**: Dispatched via `AiRemoteDataSourceImpl` (`/chat/completions`) with `temperature = 0.2` to eliminate stylistic deviation.
4. **Sanitization & Kotlinx Deserialization**: Strips markdown fences (triple backticks) and parses raw payloads with `Json.decodeFromString<AiAnalysisResponseDto>()`.
5. **Domain Transformation & Caching**: Mapped to pure domain entities (`List<Insight>`, `List<Drill>`) and cached in-memory with `AiAnalysisLocalDataSource` (`Mutex`).

> For full architectural details, sequence diagrams, and schema specifications, see [docs/architecture/ai-tactical-analysis-structured-outputs.md](docs/architecture/ai-tactical-analysis-structured-outputs.md).

<a id="section-62-dual-provider-fallback"></a>
### 6.2 Dual-Provider AI Fallback Resilience (`feature_012_gemini_fallback_resilience`)

BasketCoach implements comprehensive, zero-downtime resilience for all remote AI operations:
1. **Fallback Chaining (`AiClientConfig.fallbackConfig`)**:
   `AiClientConfig` declares an explicit `fallbackConfig: AiClientConfig?` (without default values). When both `DEVEXPERT_API_KEY` and `GEMINI_API_KEY` are provided, `NetworkModule` and `AiClientConfig.fromEnvironment()` chain Gemini as the secondary fallback for DevExpert. If the DevExpert key is absent or placeholder, direct startup with Gemini is provisioned without attempting DevExpert.
2. **Comprehensive Failover Triggers**:
   `AiRemoteDataSourceImpl.isFailoverEligible(throwable)` intercepts:
   - Subscription & Quota: HTTP 401 Unauthorized (expired key), HTTP 402 Payment Required (subscription expired/quota depleted), HTTP 403 Forbidden (account suspended), HTTP 429 Too Many Requests (rate limit).
   - Server Outages: HTTP 5xx (500, 502, 503, 504 server failures / maintenance).
   - Network Timeouts: `SocketTimeoutException`.
   Offline errors (`UnknownHostException`, `ConnectException`) are explicitly excluded to prevent false failover when internet access is absent.
3. **Session Failover Latching**:
   Upon the first failover, `@Volatile private var activeConfig` is updated to `fallbackConfig`. All subsequent requests in the session immediately route to Gemini with zero failing round-trips or timeouts against DevExpert.
4. **Dynamic Model Adaptation**:
   When falling back, requests translate DevExpert model aliases (`"chat"`, `"chat-pro"`, `"jev"`) to `fallbackConfig.model` (`"gemini-3.5-flash"`).
5. **Dual Failure Graceful Degradation**:
   If both providers fail, technical exceptions are translated by repository `safeCall` into typed `DomainError.Network.*`, transitioning ViewModels to `Content.Error` and displaying `AnalysisErrorView` / `ChatErrorView` with retry buttons.


---

## 7. Presentation & Navigation Architecture (`:app`)

The `:app` module delivers a specialized high-density user experience strictly optimized for **Android Tablets in Landscape orientation** (`1280x800dp`).

```text
+-----------------------------------------------------------------------------------------------+
| BasketCoachNavRail (80dp Navy) | BasketCoachTopBar (64dp White)                               |
|                                +--------------------------------------------------------------+
| [Dashboard]                    | Dynamic Viewport (Navigation Host)                           |
| [Anàlisi]                      |   - DashboardScreen (Phase 1 Baseline)                       |
| [Chat]                         |       ├─ DashboardHeroCard (3V-11D, 21.4%, 14 games)         |
|                                |       ├─ KpiCardRow (kpi/: Scoring, Rebounds, Shooting, Control)  |
|                                |       ├─ Phase1EvolutionChart (14-round bars, green badges)  |
|                                |       └─ Phase1MatchesTable (table/: 14 matches, 16 cols)     |
|                                |   - PlaceholderScreen (Anàlisi / Chat)                       |
+-----------------------------------------------------------------------------------------------+
```

### Key Architectural Patterns for UI & Presentation
1. **AppState & Destinations Pattern (`ui.navigation`)**:
   - `Destinations.kt` models top-level routes as Kotlin `@Serializable` objects (`DashboardRoute`, `AnalysisRoute`, `ChatRoute`).
   - `TopLevelRoute<T : Any>` encapsulates the string resource label, route instance, and Material icon vector for navigation rail items.
   - `BasketCoachAppState` holds the `NavHostController`, application `CoroutineScope`, and `SnackbarHostState`. It evaluates the active route via destination hierarchy traversal (`currentDestination?.hierarchy?.any { it.hasRoute(route::class) } == true`).
   - State-preserving tab switching is guaranteed via `navigatePoppingUpToStartDestination(route)` using `popUpTo(graph.findStartDestination().id) { saveState = true }`, `launchSingleTop = true`, and `restoreState = true`.
2. **Decoupled & Passive Composables**:
   - Entry screen composables (`DashboardScreen`, `PlaceholderScreen`) receive zero `NavController` or `BasketCoachAppState` parameters. All navigation intents and event callbacks are hoisted to callers, ensuring 100% decoupling from Jetpack Navigation.
3. **Composable Decomposition & Hierarchy**:
   - `BasketCoachApp`: Root application shell coordinating the 80dp `BasketCoachNavRail` on the left and a content column with `BasketCoachTopBar` and `Navigation(appState)`.
   - `BasketCoachTopBar`: 64dp white top bar displaying the application title, category pill ("Junior Masculí Interterritorial"), verified dataset status pill, and Head Coach profile avatar with name.
   - `DashboardContent`: Vertically scrollable column assembling the four primary dashboard cards (`DashboardHeroCard`, `KpiCardRow`, `Phase1EvolutionChart`, `Phase1MatchesTable`).
4. **Strict Default-Free & Non-Nullable UI State Models (`ui.dashboard.model`)**:
   - `DashboardUiState` and its constituent sub-models (`Phase1SummaryUiModel`, `DashboardKpisUiModel`, `ScoringEvolutionUiModel`, `MatchRowUiModel`) model all UI properties as non-nullable types with **zero default parameter values**.
   - UI mappers and static data providers (`Phase1PreviewData`) serve as the sole authoritative source of UI state, preventing accidental fallback bugs or silent omissions.
5. **Design System & Typographic Governance (`ui.theme`)**:
   - Strictly enforces a minimum typography size of `12.sp` across all labels, pills, chart axis texts, and table cells to guarantee readability on physical tablet displays.
   - Reusable preview annotations (`@BcThemePreview` and `@BcDevicePreview`) validate the Pia Sabadell brand theme across target tablet landscape form factors (Tablet 10" Landscape baseline and Tablet 12" Large Landscape) for all composables without redundant or broken configurations.


### 7.1 Dashboard Integration Architecture (`feature_008_dashboard_integration`)

The Phase 1 Dashboard connects to authentic domain fixtures through a decoupled MVVM integration adhering strictly to clean architectural boundaries:

1. **Hilt Dependency Injection Setup (`di`)**:
   - `BasketCoachApplication` triggers Hilt root generation via `@HiltAndroidApp`.
   - `DataSourceModule` and `RepositoryModule` install into `SingletonComponent` using abstract `@Binds` methods (`bindGameLocalDataSource` and `bindGameRepository`) for zero-cost static delegates rather than runtime factory allocations.
   - Core domain `:domain` remains 100% pure Kotlin JVM (zero DI dependencies). `:usecases` uses JSR-330 `javax.inject.Inject` on `GetPhase1GamesUseCase` without any Android SDK or Hilt Gradle plugins.

2. **Passive ViewModel Lifecycle & Hybrid UI State (`DashboardViewModel`)**:
   - Adheres strictly to the **Passive Initialization Mandate**: zero coroutines or repository queries are launched in constructor or `init` blocks. Initial state defaults passively to `DashboardUiContent.Loading`.
   - Data loading is explicitly triggered from Compose via `LaunchedEffect(Unit) { viewModel.loadDashboard() }`.
   - `DashboardUiState(val content: DashboardUiContent, val isRefreshing: Boolean)` models mutually exclusive states (`Loading`, `Success`, `Error(DomainError)`) while retaining the additive `isRefreshing` overlay flag.
   - Idempotent concurrency guarding prevents duplicate in-flight requests (`if (_uiState.value.isRefreshing) return`).
   - All state mutations are performed atomically via `MutableStateFlow.update { ... }`.

3. **Authoritative Mathematical Transformation (`DashboardUiMapper`)**:
   - `DashboardUiMapper` is the single source of truth for presentation calculations. It transforms raw domain `Game` entities into `DashboardUiContent.Success`:
     - Summary record: 3V-11D, 21.4% win rate over 14 games.
     - 4 KPI pillars: Scoring (64.6 vs 73.7, net -9.1), Rebounds (22.8 vs 30.4, net -7.6, off 6.3, def 16.5), Shooting (eFG% 40.5%, 2P 43.4%, 3P 25.4%, FT 56.4%), and Game Control (AST/TO 0.65, assists 10.3, turnovers 17.6).
     - 14-round scoring progression bars with win flags for J3, J9, and J10.
     - 14-row statistical table across 16 statistical columns with normalized team names.

4. **Zero-Resource-Leakage Error Handling (`UiText`)**:
   - `DashboardViewModel` exposes raw `DomainError` in `DashboardUiContent.Error(error = result.error)`. It contains zero imports of Android `R.string`.
   - Error string translation occurs strictly in the UI layer via `DomainError.toDashboardUiText(): UiText`, mapping `DomainError.Local.NotFound` to `R.string.dashboard_error_empty` and unexpected failures to `R.string.dashboard_error_generic`.

5. **Pragmatic Pure JVM Presentation Testing (Constraint #18)**:
   - Presentation tests are 100% pure JVM unit tests without Robolectric:
     - `DashboardUiMapperTest` (2 tests): Mathematical calculation integrity and empty list exception boundary.
     - `DashboardViewModelTest` (2 tests): Passive initialization, successful data loading, error handling, and retry recovery.

---

## 8. Tactical Analysis Presentation Architecture (`:app`)

The Analysis screen (`AnalysisScreen`) provides Coach Jordi with an actionable, high-density tactical diagnosis of Phase 1 competition findings, optimized for Tablet Landscape (`1280x800dp`).

```text
+-----------------------------------------------------------------------------------------------+
| BasketCoachNavRail (80dp Navy) | BasketCoachTopBar (64dp White)                               |
|                                +--------------------------------------------------------------+
| [Dashboard]                    | Dynamic Viewport: AnalysisScreen                             |
| [Anàlisi]                      |   ├─ AnalysisHeroCard (3V-11D, 21.4%, 14/14 Jornades)        |
| [Chat]                         |   ├─ AnalysisFilterTabs (Tots [5] | Fortaleses [2] | Millora [3])|
|                                |   └─ InsightCardGrid (3-Column Tablet Landscape Grid)        |
|                                |        ├─ InsightCard (Fortalesa: Eficiència a la Pintura)   |
|                                |        ├─ InsightCard (Fortalesa: Transició Ràpida)          |
|                                |        ├─ InsightCard (Àrea de Millora: Rebot Defensiu)      |
|                                |        ├─ InsightCard (Àrea de Millora: Pèrdues Q3)          |
|                                |        └─ InsightCard (Àrea de Millora: Tirs Lliures Finals) |
+-----------------------------------------------------------------------------------------------+
```

### 8.1 Tactical Analysis MVVM & Real Data Integration (`feature_009_analysis_integration`)

The Analysis screen connects to authentic domain match data and AI-generated tactical insights through a decoupled, high-performance MVVM presentation pipeline:

1. **Hilt Dependency Injection & Network Provisioning (`di`)**:
   - `NetworkModule` (`@Module @InstallIn(SingletonComponent::class) object NetworkModule`) provisions application-wide singletons:
     - `provideAiClientConfig()`: Resolves `AiClientConfig` with base URL, API keys (`DEVEXPERT_API_KEY` / `GEMINI_API_KEY` from `BuildConfig` / environment variables), and default models.
     - `provideOkHttpClient()`: Supplies standard OkHttpClient preconfigured with suitable network timeouts (30s connect, 60s read).
     - `provideJson()`: Provides lenient Kotlinx Serialization `Json` engine.
   - `DataSourceModule`: Binds `AiAnalysisLocalDataSourceImpl` to `AiAnalysisLocalDataSource` and `AiRemoteDataSourceImpl` to `AiRemoteDataSource` via abstract `@Binds @Singleton`.
   - `RepositoryModule`: Binds `AiAnalysisRepositoryImpl` to `AiAnalysisRepository` via abstract `@Binds @Singleton`.
   - Pure Kotlin JVM boundaries in `:domain`, `:usecases`, and `:data` remain 100% untouched, using JSR-330 `@Inject` without Android framework dependencies.

2. **Passive ViewModel Lifecycle & Hybrid UI State (`AnalysisViewModel`)**:
   - Strictly enforces the **Passive Initialization Mandate**: zero coroutines, network calls, or repository queries execute in `init`. Initial state is passively initialized to `AnalysisUiState(content = AnalysisUiContent.Loading, isRefreshing = false)`.
   - Initial data loading is explicitly invoked from Compose via `LaunchedEffect(Unit) { viewModel.loadAnalysis() }` in stateful `AnalysisScreen`.
   - Incorporates an idempotent concurrency guard (`if (_uiState.value.isRefreshing) return`) to eliminate duplicate in-flight requests.
   - Hybrid UI State: `AnalysisUiState(val content: AnalysisUiContent, val isRefreshing: Boolean)` models discrete states (`AnalysisUiContent.Loading`, `AnalysisUiContent.Success`, `AnalysisUiContent.Error(DomainError)`) coupled with the additive refresh indicator.
   - State updates are performed atomically via `MutableStateFlow.update { ... }`.
   - In-memory tab filtering (`onFilterSelected(filter: InsightFilterTab)`) dynamically filters `allInsights` into `displayedInsights` without re-querying remote or local data sources, preserving active tab selection across manual refreshes.

3. **Authoritative Presentation Transformation (`AnalysisUiMapper`)**:
   - Single source of truth for tactical UI transformations:
     - Aggregates Phase 1 record baseline (`Phase1SummaryUiModel`: 3V-11D, 21.4% win rate). Returns `AnalysisUiContent.Error(DomainError.Local.NotFound)` if games list is empty.
     - Maps domain `Insight` entities into presentation `InsightCardUiModel` instances. Replaces raw string badges with localized `@StringRes categoryBadgeRes` (`R.string.analysis_badge_strength` or `R.string.analysis_badge_improvement`).
     - Computes tab counts (`totalCount`, `strengthsCount`, `improvementsCount`) and applies the active `InsightFilterTab`.
     - Evaluates chat explore eligibility (`canExploreChat = insight.type == InsightType.IMPROVEMENT && insight.exploreChatPrompt.isNotBlank()`).

4. **Zero-Resource-Leakage Error Handling (`toAnalysisUiText`)**:
   - ViewModels and Mappers expose raw `DomainError` in `AnalysisUiContent.Error(error = result.error)` without importing Android `R.string`.
   - Translation to user-visible strings occurs in the UI layer via `DomainError.toAnalysisUiText(): UiText`, translating `Local.NotFound`, `Network.NoInternet`, `Network.Timeout`, `Network.Unauthorized`, `Network.ServerError`, and generic failures into localized string resources or dynamic server messages.

5. **Pragmatic Pure JVM Presentation Testing (Constraint #18)**:
   - Presentation tests adhere strictly to the pure JVM testing budget without Robolectric UI test bloat:
     - `AnalysisUiMapperTest` (2 tests): Valid data transformation to `Success` with in-memory tab filtering; empty games list error handling.
     - `AnalysisViewModelTest` (2 tests): Passive initialization, state transitions to `Success` and tab filtering; domain error handling and retry recovery.

---

## 9. Tablet Socratic Chat UI Architecture (`:app`)

The Chat screen (`ChatScreen`) provides Coach Jordi with a high-density, tablet-optimized conversational AI workbench for translating Phase 1 competition findings (3V-11D) into concrete Phase 2 training microcycles and drill commitments.

```text
+-----------------------------------------------------------------------------------------------+
| BasketCoachNavRail | ChatMetaHeader: Espai de Reflexió Tàctica · 1a Fase | 3V-11D | Junior A  |
| (80dp Pia Navy)    +--------------------------------------------------------------------------+
|                    | LEFT WORKBENCH PANE (4 Cols)       | RIGHT CONVERSATION PANE (8 Cols)     |
|                    | ChatTopicList (Persistent, Scroll) | ChatRightPane:                       |
|                    |   ├─ Priority Area: Rebot Defensiu |   ├─ Header: BasketCoach Socràtic    |
|                    |   ├─ Area 2: Pèrdues en Transició  |   ├─ ChatMessageStream:              |
|                    |   ├─ Area N: AI Improvement Area   |   │    ├─ AiMessageBubble            |
|                    |   └─ Open Inquiry (Consulta Lliure)|   │    │    ├─ Callout Question      |
|                    |                                    |   │    │    └─ Drill Options (A / B) |
|                    |                                    |   │    ├─ CoachMessageBubble         |
|                    |                                    |   │    └─ AssistantSummaryCard       |
|                    |                                    |   │         (Phase 2 Commitment)     |
|                    |                                    |   └─ ChatInputBar:                   |
|                    |                                    |        [Text Field] [Send (48dp)]    |
+-----------------------------------------------------------------------------------------------+
```

### Key Architectural Patterns for Chat UI
1. **Persistent Workbench Left Pane with Real Tactical Improvement Areas (`feature_011_chat_improvement_areas`)**:
   - The left workbench pane (`ChatLeftPane`) permanently renders `ChatTopicList` without mode switching or modal transitions.
   - Powered by `GenerateInsightsUseCase` (sharing the `@Singleton AiAnalysisLocalDataSource` cache with `AnalysisScreen`), the topic list dynamically populates all AI-detected `InsightType.IMPROVEMENT` areas plus the final `CONSULTA LLIURE` ("Open Inquiry") card (`OPEN_INQUIRY_TOPIC_ID = "open_inquiry"`).
   - The topic card column uses `verticalScroll(rememberScrollState())` to adaptively accommodate any number of improvement findings on the tablet landscape display (`1280x800dp`).
2. **Topic-Scoped Conversation Threads (`topicId: String`) (Constraint #4 & #6)**:
   - Selecting any improvement area or open inquiry card (`onTopicSelected(topic)`) sets `activeTopicId = topic.id` with high-contrast active styling (`PiaTacticalOrange`, 1.5dp border) and updates the right pane (`ChatRightPane`) to load and continue that specific topic's thread.
   - Message histories are cleanly partitioned in memory across all architectural layers by `topicId`, preventing conversation cross-talk between different tactical intervention areas.
3. **Seamless Exploration Hand-off from Analysis Screen**:
   - `ChatRoute` declares `@Serializable data class ChatRoute(val initialPrompt: String = "")`.
   - When Coach Jordi taps "Explorar al Xat" on an improvement card in `AnalysisScreen`, `ChatViewModel.loadChat(initialPrompt)` matches `initialPrompt` against loaded improvement insights (`exploreChatPrompt == initialPrompt || id == initialPrompt`), automatically selects that topic as active, and dispatches the inquiry into its thread once via an `initialPromptProcessed` concurrency latch.
4. **Interactive Socratic Drill Choices & Agreement Cards**:
   - `AiMessageBubble` embeds interactive `AssistantDrillOptionCard` choices (Option A vs Option B) with merged accessibility semantics (`mergeDescendants = true`).
   - Tapping an option appends Coach Jordi's confirmation message and triggers an AI response containing the `AssistantSummaryCard` synthesizing the weekly practice commitment (e.g., "Capacitat de Tancar el Rebot Defensiu", < 9.5 pts 2a oportunitat, 4 setmanes).
5. **Strict Default-Free & Non-Nullable UI Models (Constraint #17)**:
   - All presentation models in `ui.chat.model` (`ChatUiState`, `ChatMetaHeaderUiModel`, `ChatTopicCardUiModel`, `ChatMessageUiModel`, `ChatDrillOptionUiModel`, `ChatSummaryCardUiModel`) enforce 0 defaults and 0 nullable properties. Absent card attachments use sentinel objects (`ChatSummaryCardUiModel.EMPTY`).
6. **Minimum Typography Governance (Constraint #16)**:
   - All text rendered across topic cards, metric badges, bubbles, and input fields enforces a minimum size of `12.sp` (never `10.sp` or `11.sp`).


## 10. Conversational Assistant & Guardrail Architecture (`feature_010_chat_integration`, `feature_011_chat_improvement_areas`)

The conversational AI assistant connects the Tablet Landscape Chat workbench (`ChatScreen`) to live inference via `SendChatMessageUseCase`, `GetChatMessagesUseCase`, `GenerateInsightsUseCase`, and `GetPhase1GamesUseCase`, featuring a **2-Step AI Guardrail Pipeline** and **Topic-Partitioned In-Memory SSOT**:

```text
               +-------------------------------------------------------------+
               | ChatViewModel.sendMessage(messageText, activeTopicId)       |
               +-------------------------------------------------------------+
                                              |
                                              v
                              +-------------------------------+
                              | SendChatMessageUseCase        |
                              | (topicId: String)             |
                              +-------------------------------+
                                              | (games context, topicId)
                                              v
                              +-------------------------------+
                              | ChatRepositoryImpl            |
                              +-------------------------------+
                                              |
                     +------------------------+------------------------+
                     | Step 1: Pre-Classification Guardrail            |
                     v                                                 v
         [ DevExpert Jev Decisions ]                     [ Gemini JSON Fallback ]
         POST /v1/decisions (model="jev")                POST /chat/completions (temp=0.0)
         {"domain_relevance": "choice"}                  {"choice": "basketball_relevant"}
                     |                                                 |
                     +------------------------+------------------------+
                                              |
                                    Is basketball relevant?
                                     /                      \
                             NO (Off-Topic)            YES (Tactical Query)
                                   /                          \
                                  v                            v
               +------------------------------------+   +------------------------------------+
               | Short-Circuit Refusal Bubble       |   | Step 2: Generative Inference       |
               | (PromptTemplates.buildOffTopic...) |   | POST /chat/completions             |
               | Zero /chat/completions tokens used |   | Grounded with 14 Phase 1 boxscores |
               +------------------------------------+   +------------------------------------+
                                  \                            /
                                   v                          v
                              +-------------------------------+
                              | Save to ChatLocalDataSource   |
                              | (Mutex, Map<topicId, List>)   |
                              +-------------------------------+
                                              |
                                              v
                              +-------------------------------+
                              | ChatViewModel Reload Stream   |
                              | (activeTopicId thread)        |
                              +-------------------------------+
```

### Key Architectural Patterns for Chat Integration

> Detailed specification: See [docs/architecture/ai-chat-architecture.md](docs/architecture/ai-chat-architecture.md).
1. **2-Step AI Domain Guardrail Pipeline**:
   - **DevExpert Jev Decisions Mode**: When `config.baseUrl` targets DevExpert Inference, pre-classifies user prompts via `POST /v1/decisions` with `model = "jev"`. The request passes the domain state (`app_domain`, `last_assistant_message`, `coach_message`) and the `"domain_relevance"` question with `"basketball_relevant"` vs `"off_topic"` criteria.
- **Google Gemini Fallback Mode**: When using Gemini Free Tier or when DevExpert `/decisions` fails with an eligible error (401/402/403/429/5xx/timeout), `AiRemoteDataSourceImpl` seamlessly switches `activeConfig` to Gemini and executes a lightweight zero-shot classification JSON call (`temperature = 0.0`, `model = "gemini-3.5-flash"`) to `/chat/completions` returning `{"choice": "basketball_relevant" | "off_topic"}`.
   - **Off-Topic Short-Circuit**: When deemed off-topic, `ChatRepositoryImpl.sendMessage` short-circuits immediately without calling `remoteDataSource.generateChatResponse`, saving expensive generative tokens on the 14-game context. A localized tactical refusal message (`ChatResourceProvider.getOffTopicRejectionMessage()`) is saved directly to `ChatLocalDataSource` under `topicId` and returned as `MessageSender.AI`.
2. **Topic-Scoped In-Memory Session Governance (Constraint #4 & #6)**:
   - `ChatLocalDataSourceImpl` manages isolated conversation threads using a `Mutex`-guarded `mutableMapOf<String, MutableList<ChatMessage>>()`.
   - Both `ChatLocalDataSource` and `ChatRepository` are bound as `@Singleton` in `DataSourceModule` and `RepositoryModule`, preserving thread state across screen navigation.
3. **Shared AI Insights Cache & Direct Chat Navigation**:
   - `ChatViewModel` injects `GenerateInsightsUseCase`.
   - Because `AiAnalysisLocalDataSource` is bound as `@Singleton`, if Coach Jordi visits `Anàlisi` first, `generateInsightsUseCase()` resolves synchronously from the in-memory cache with 0 duplicate network calls. If Coach Jordi navigates directly to `Chat` first, `generateInsightsUseCase()` fetches insights from the AI service and populates the shared cache for later use in `Anàlisi`.
4. **Passive ViewModel Lifecycle & Exploration Hand-off (`ChatViewModel`)**:
   - Strictly satisfies the **Passive Initialization Mandate**: zero coroutines or repository queries run in `init`. Initial state is `ChatUiState(content = ChatUiContent.Loading, isSending = false)`.
   - Data loading is triggered via `LaunchedEffect(initialPrompt) { viewModel.loadChat(initialPrompt) }` in `ChatScreen`.
   - "Explorar al Xat" hand-off: matches `initialPrompt` to an `InsightType.IMPROVEMENT` topic, highlights it, and auto-dispatches the prompt once into that topic's thread.
5. **Hybrid UI State Pattern**:
   - `ChatUiState(val content: ChatUiContent, val isSending: Boolean)` models discrete states (`Loading`, `Success`, `Error(DomainError)`) while providing the additive `isSending` overlay flag.
   - While `isSending` is true, user input is disabled and a loading indicator is rendered over the message stream. Concurrent send requests are dropped (`if (trimmed.isBlank() || _uiState.value.isSending) return`).
6. **Decoupled Mathematical Transformation & Resource Parity**:
   - `ChatUiMapper` calculates 3V-11D meta header stats, maps `InsightType.IMPROVEMENT` to dynamic topic cards with deterministic priority (`isPriority = index == 0`), appends `OPEN_INQUIRY_TOPIC_ID`, and maps domain messages to localized sender roles without hardcoded strings.
   - `toChatUiText(): UiText` translates `DomainError` into localized strings with zero `R.string` imports in `ChatViewModel`.
   - 100% key parity is enforced between Catalan (`values/strings.xml`) and Spanish (`values-es/strings.xml`).
7. **Pragmatic Pure JVM Testing Budget (Constraint #18)**:
   - Presentation, repository, and usecase logic are verified by pure JVM tests without Robolectric:
     - `ChatRepositoryImplTest` (2 tests): Off-topic prompt short-circuiting asserting 0 calls to chat completions; topic-scoped thread isolation asserting independent message history across distinct `topicId` keys.
     - `SendChatMessageUseCaseTest` (3 tests): Contextual game grounding orchestration, graceful fallback when game repository fails, and error propagation.
     - `ChatUiMapperTest` (2 tests): Dynamic `InsightType.IMPROVEMENT` mapping + Open Inquiry card with deterministic priority; empty games error guard.
     - `ChatViewModelTest` (3 tests): Passive initialization with `GenerateInsightsUseCase` and `initialPrompt` topic matching/dispatch; topic thread switching; error handling and retry recovery.
