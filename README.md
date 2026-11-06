# BasketCoach

BasketCoach is a Tablet Landscape application for a Junior Men's Interterritorial basketball coach. It analyzes 14 past games from Phase 1 (static dataset) to extract insights, strengths, weaknesses, and practice drills for Phase 2 using the DevExpert API, and includes a single-thread AI chat assistant.

## Architecture

See [ARCHITECTURE.md](ARCHITECTURE.md) for complete system topology, module governance, and design patterns.

- **UI Pattern**: `MVVM (Hybrid UI State & Passive Initialization)` (Governed by `.agents/skills/viewmodel-architecture-governance/SKILL.md`)
- **Module Topology**: `Layer-Based (:domain, :usecases, :data, :app)`
  - `:domain` -> `:usecases` <- `:data` -> `:app`
- **Dependency Injection**: `Hilt`
- **Persistence**: `Room`
- **AI Provider**: `Gemini` (DevExpert OpenAI-compatible proxy)
- **Target Form Factor**: Android Tablet Landscape (`1280x800dp` baseline, minimum typography `12.sp`)

## Modules

- `:domain` — Pure Kotlin layer defining basketball entities, value objects, domain errors, and repository interfaces.
- `:data` — Android/Kotlin data layer with Room persistence, static JSON asset parsing, DevExpert API client, and repository implementations.
- `:usecases` — Domain interactors coordinating business logic and AI analysis.
- `:app` — Jetpack Compose presentation layer strictly optimized for tablet landscape orientation.

## AI Tactical Analysis & Structured Outputs

BasketCoach integrates AI via DevExpert Inference (with Google Gemini fallback) using the **Structured Outputs Pattern**:
- **Empirical Grounding**: The AI is strictly anchored in the 14 Phase 1 box scores (`PromptTemplates.formatGameSummary`), preventing hallucinations.
- **Strict Schema Contract**: System prompts mandate valid JSON matching `AiAnalysisResponseDto` without markdown wrapping or null fields.
- **Deterministic Inference**: Queries are dispatched at `temperature = 0.2` for maximum schema reliability.
- **Type-Safe Domain Modeling**: Raw JSON is deserialized with `kotlinx.serialization`, mapped to pure Kotlin domain models (`Insight`, `Drill`), and cached in-memory with thread-safe `Mutex` locking.
- **Dual-Provider Resilience**: Automatically falls back to Google Gemini (`gemini-3.8-flash`) on DevExpert subscription expiration (HTTP 402), authentication errors (HTTP 401/403), server outages (HTTP 5xx), or network timeouts with session latching.

For full architectural details, see the deep dive in [docs/architecture/ai-tactical-analysis-structured-outputs.md](docs/architecture/ai-tactical-analysis-structured-outputs.md) and [ARCHITECTURE.md](ARCHITECTURE.md) (Section 6.1).

## Progress & Status
- [x] **`feature_001_domain_models`**: Pure Kotlin domain models and repository interfaces in `:domain` (`accepted`).
- [x] **`feature_002_data_parsing`**: Phase 1 JSON dataset parsing and cached `GameRepositoryImpl` in `:data` (`accepted`).
- [x] **`feature_003_devexpert_client`**: DevExpert & Gemini OpenAI-compatible API client, data sources, and repositories in `:data` (`accepted`).
- [x] **`feature_004_usecases`**: Core business interactors in `:usecases` for game queries, AI tactical insights, and conversational chat (`accepted`).
- [x] **`feature_005_dashboard_ui`**: Tablet Landscape Dashboard UI (80dp NavRail, 64dp TopBar, Hero card, 4 KPI cards, 14-round evolution chart, 14-match table) in `:app` (`accepted`).
- [x] **`feature_006_analysis_ui`**: Tablet Landscape Analysis UI (hero card, 3-filter segmented control, tactical insight cards with metric panels) in `:app` (`accepted`).
- [x] **`feature_007_chat_ui`**: Tablet Single-Thread Chat UI (continuous message history, prompt reflections, AI responses) in `:app` (`accepted`).
- [x] **`feature_008_dashboard_integration`**: Connect Dashboard to Real Data via `GetPhase1GamesUseCase`, `DashboardViewModel`, and Hilt DI in `:app` (`accepted`).
- [x] **`feature_009_analysis_integration`**: Connect Analysis to Real Data via `GenerateInsightsUseCase` and ViewModel in `:app` (`accepted`).
- [x] **`feature_010_chat_integration`**: Connect Chat to DevExpert API via `SendChatMessageUseCase` and ViewModel in `:app` (`accepted`).
- [x] **`feature_011_chat_improvement_areas`**: Synchronize Chat Left Pane with Real Analysis Improvement Areas & Topic-Scoped Threads in `:app`, `:usecases`, `:data`, `:domain` (`accepted`).
- [x] **`feature_012_gemini_fallback_resilience`**: Gemini Fallback & DevExpert Subscription Expiration Resilience in `:data` and `:app` (`accepted`).
