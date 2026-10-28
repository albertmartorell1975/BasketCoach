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
   - `safeCall` preserves structured concurrency by immediately rethrowing `CancellationException`.


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
   - Reusable preview annotations (`@BcThemePreview` and `@BcDevicePreview`) validate themes (Light, Dark, RTL, Large Font) and form factors (Tablet Landscape, Phone Landscape) across all composables.


### 7.1 Dashboard Integration Architecture (eature_008_dashboard_integration)

The Phase 1 Dashboard connects to authentic domain fixtures through a decoupled MVVM integration adhering strictly to clean architectural boundaries:

1. **Hilt Dependency Injection Setup (di)**:
   - BasketCoachApplication triggers Hilt root generation via @HiltAndroidApp.
   - DataSourceModule and RepositoryModule install into SingletonComponent using abstract @Binds methods (indGameLocalDataSource and indGameRepository) for zero-cost static delegates rather than runtime factory allocations.
   - Core domain :domain remains 100% pure Kotlin JVM (zero DI dependencies). :usecases uses JSR-330 javax.inject.Inject on GetPhase1GamesUseCase without any Android SDK or Hilt Gradle plugins.

2. **Passive ViewModel Lifecycle & Hybrid UI State (DashboardViewModel)**:
   - Adheres strictly to the **Passive Initialization Mandate**: zero coroutines or repository queries are launched in constructor or init blocks. Initial state defaults passively to DashboardUiContent.Loading.
   - Data loading is explicitly triggered from Compose via LaunchedEffect(Unit) { viewModel.loadDashboard() }.
   - DashboardUiState(val content: DashboardUiContent, val isRefreshing: Boolean) models mutually exclusive states (Loading, Success, Error(DomainError)) while retaining the additive isRefreshing overlay flag.
   - Idempotent concurrency guarding prevents duplicate in-flight requests (if (_uiState.value.isRefreshing) return).
   - All state mutations are performed atomically via MutableStateFlow.update { ... }.

3. **Authoritative Mathematical Transformation (DashboardUiMapper)**:
   - DashboardUiMapper is the single source of truth for presentation calculations. It transforms raw domain Game entities into DashboardUiContent.Success:
     - Summary record: 3V-11D, 21.4% win rate over 14 games.
     - 4 KPI pillars: Scoring (64.6 vs 73.7, net -9.1), Rebounds (22.8 vs 30.4, net -7.6, off 6.3, def 16.5), Shooting (eFG% 40.5%, 2P 43.4%, 3P 25.4%, FT 56.4%), and Game Control (AST/TO 0.65, assists 10.3, turnovers 17.6).
     - 14-round scoring progression bars with win flags for J3, J9, and J10.
     - 14-row statistical table across 16 statistical columns with normalized team names.

4. **Zero-Resource-Leakage Error Handling (UiText)**:
   - DashboardViewModel exposes raw DomainError in DashboardUiContent.Error(error = result.error). It contains zero imports of Android R.string.
   - Error string translation occurs strictly in the UI layer via DomainError.toDashboardUiText(): UiText, mapping DomainError.Local.NotFound to R.string.dashboard_error_empty and unexpected failures to R.string.dashboard_error_generic.

5. **Pragmatic Pure JVM Presentation Testing (Constraint #18)**:
   - Presentation tests are 100% pure JVM unit tests without Robolectric:
     - DashboardUiMapperTest (2 tests): Mathematical calculation integrity and empty list exception boundary.
     - DashboardViewModelTest (2 tests): Passive initialization, successful data loading, error handling, and retry recovery.

---

## 8. Tactical Analysis Presentation Architecture (`:app`)

The Analysis screen (`AnalysisScreen`) provides Coach Jordi with an actionable, high-density tactical diagnosis of Phase 1 competition findings, optimized for Tablet Landscape (`1280x800dp`).


---

## 9. Tablet Socratic Chat UI Architecture (`:app`)

The Chat screen (`ChatScreen`) provides Coach Jordi with a high-density, tablet-optimized conversational AI workbench for translating Phase 1 competition findings (3V-11D) into concrete Phase 2 training microcycles and drill commitments.

```text
+-----------------------------------------------------------------------------------------------+
| BasketCoachNavRail | ChatMetaHeader: Espai de Reflexió Tàctica · 1a Fase | 3V-11D | Junior A  |
| (80dp Pia Navy)    +--------------------------------------------------------------------------+
|                    | LEFT WORKBENCH PANE (4 Cols)       | RIGHT CONVERSATION PANE (8 Cols)     |
|                    | Mode A: ChatTopicList              | ChatRightPane:                       |
|                    |   ├─ Topic 1: Rebot Defensiu       |   ├─ Header: BasketCoach Socràtic    |
|                    |   ├─ Topic 2: Ràtio AST/TO         |   ├─ ChatMessageStream:              |
|                    |   ├─ Topic 3: Pèrdues Q3           |   │    ├─ AiMessageBubble            |
|                    |   └─ Topic 4: Xat Obert            |   │    │    ├─ Callout Question      |
|                    |                                    |   │    │    └─ Drill Options (A / B) |
|                    | Mode B: ChatDiagnosticEvidenceView |   │    ├─ CoachMessageBubble         |
|                    |   ├─ 2-Metric Box Score Bar        |   │    └─ AssistantSummaryCard       |
|                    |   ├─ Zero-Assumption Rule Card     |   │         (Phase 2 Commitment)     |
|                    |   ├─ ChatHalfCourtCanvas (Compose) |   └─ ChatInputBar:                   |
|                    |   └─ "← Tots els fils" Toggle      |        [Text Field] [Send (48dp)]    |
+-----------------------------------------------------------------------------------------------+
```

### Key Architectural Patterns for Chat UI
1. **Two-Pane Tablet Workbench with Seamless Mode Switching**:
   - The left pane operates as a stateful coordinator (`ChatLeftPane`) alternating between `ChatLeftPaneMode.TOPIC_LIST` (the 4 tactical intervention threads) and `ChatLeftPaneMode.DIAGNOSTIC_EVIDENCE` (in-depth box score metrics, Zero-Assumption rule validation, and custom Compose `ChatHalfCourtCanvas` court visualizer).
   - A prominent "← Tots els fils" navigation toggle allows Coach Jordi to seamlessly return to the overview without losing dialogue context.
2. **Single Continuous Thread Mandate (Constraint #6)**:
   - Selecting an intervention topic card pre-grounds or filters ongoing analytical context within a single continuous conversation stream rather than creating fragmented multi-session tabs or database session splits.
3. **Diagnostic Evidence Hand-off from Analysis Screen**:
   - `ChatRoute` declares `@Serializable data class ChatRoute(val initialPrompt: String = "")`.
   - When Coach Jordi taps "Explorar al Xat" on an Analysis card, `navigateToChat(prompt)` passes the intent. `ChatScreen` pre-fills the prompt, automatically switches the left pane to `ChatLeftPaneMode.DIAGNOSTIC_EVIDENCE`, and activates the corresponding intervention focus.
4. **Interactive Socratic Drill Choices & Agreement Cards**:
   - `AiMessageBubble` embeds interactive `AssistantDrillOptionCard` choices (Option A vs Option B) with merged accessibility semantics (`mergeDescendants = true`).
   - Tapping an option appends Coach Jordi's confirmation message and triggers an AI response containing the `AssistantSummaryCard` synthesizing the weekly practice commitment (e.g., "Capacitat de Tancar el Rebot Defensiu", < 9.5 pts 2a oportunitat, 4 setmanes).
5. **Strict Default-Free & Non-Nullable UI Models (Constraint #17)**:
   - All 8 UI presentation models in `ui.chat.model` (`ChatUiState`, `ChatMetaHeaderUiModel`, `ChatTopicCardUiModel`, `ChatDiagnosticEvidenceUiModel`, `ChatMessageUiModel`, `ChatDrillOptionUiModel`, `ChatSummaryCardUiModel`, `ChatLeftPaneMode`) enforce 0 defaults and 0 nullable properties. Absent card attachments use sentinel objects (`ChatSummaryCardUiModel.EMPTY`).
6. **Minimum Typography Governance (Constraint #16)**:
   - All text rendered across topic cards, metric badges, half-court diagram labels, bubbles, and input fields enforces a minimum size of `12.sp` (never `10.sp` or `11.sp`).
