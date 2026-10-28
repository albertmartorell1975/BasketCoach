# BasketCoach

BasketCoach is a Tablet Landscape application for a Junior Men's Interterritorial basketball coach. It analyzes 14 past games from Phase 1 (static dataset) to extract insights, strengths, weaknesses, and practice drills for Phase 2 using the DevExpert API, and includes a single-thread AI chat assistant.

## Architecture

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

## Progress & Status
- [x] **`feature_001_domain_models`**: Pure Kotlin domain models and repository interfaces in `:domain` (`accepted`).
- [x] **`feature_002_data_parsing`**: Phase 1 JSON dataset parsing and cached `GameRepositoryImpl` in `:data` (`accepted`).
- [x] **`feature_003_devexpert_client`**: DevExpert & Gemini OpenAI-compatible API client, data sources, and repositories in `:data` (`accepted`).
- [x] **`feature_004_usecases`**: Core business interactors in `:usecases` for game queries, AI tactical insights, and conversational chat (`accepted`).
- [x] **`feature_005_dashboard_ui`**: Tablet Landscape Dashboard UI (80dp NavRail, 64dp TopBar, Hero card, 4 KPI cards, 14-round evolution chart, 14-match table) in `:app` (`accepted`).
- [x] **`feature_006_analysis_ui`**: Tablet Landscape Analysis UI (hero card, 3-filter segmented control, tactical insight cards with metric panels) in `:app` (`accepted`).
- [x] **`feature_007_chat_ui`**: Tablet Single-Thread Chat UI (continuous message history, prompt reflections, AI responses) in `:app` (`accepted`).
- [x] **`feature_008_dashboard_integration`**: Connect Dashboard to Real Data via `GetPhase1GamesUseCase`, `DashboardViewModel`, and Hilt DI in `:app` (`accepted`).
- [ ] **`feature_009_analysis_integration`**: Connect Analysis to Real Data via `GenerateInsightsUseCase` and ViewModel in `:app` (`not_started`).
- [ ] **`feature_010_chat_integration`**: Connect Chat to DevExpert API via `SendChatMessageUseCase` and ViewModel in `:app` (`not_started`).
