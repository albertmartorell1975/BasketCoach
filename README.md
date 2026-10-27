# Basket Coach
An AI-assisted coaching tool that transforms structured team statistics into evidence-based analysis, coaching questions and actionable training focuses.

## Architecture

BasketCoach follows a **Layer-Based Clean Architecture** pattern across four primary modules:
- **`:domain`**: Core business entities (`Game`, `TeamStats`, `Score`, `Venue`, `Insight`, `Drill`), typed errors (`DomainError`), and repository contracts (`GameRepository`, `AiAnalysisRepository`, `ChatRepository`). Pure Kotlin JVM with zero external dependencies.
- **`:usecases`**: Pure Kotlin JVM domain interactors (`GetPhase1GamesUseCase`, `GetGameByRoundUseCase`, `GenerateInsightsUseCase`, `GetPracticeDrillsUseCase`, `GetChatMessagesUseCase`, `SendChatMessageUseCase`) orchestrating analytical and conversational workflows with strict dependency solely on `:domain`.
- **`:data`**: Repository implementations, local fixture data sources (`GameLocalDataSource`), in-memory SSOT caching, and OpenAI-compatible AI client (`AiRemoteDataSource`) supporting DevExpert and Google Gemini.
- **`:app`**: Tablet Landscape Jetpack Compose UI (two-pane dashboard, navigation shell, and single-thread chat assistant) using Hilt and MVVM Hybrid UI State.

## AI Configuration & Credentials

The application uses an OpenAI-compatible client supporting two providers:
1. **DevExpert Inference API** (`https://inference.devexpert.io/v1`, models `chat` / `chat-pro`)
2. **Google Gemini** (`https://generativelanguage.googleapis.com/v1beta/openai`, model `gemini-2.5-flash`)

To supply API keys for development, add either or both keys to your root `local.properties`:
```properties
DEVEXPERT_API_KEY=your_devexpert_api_key_here
GEMINI_API_KEY=your_gemini_api_key_here
```
Alternatively, set them as environment variables or system properties. If `DEVEXPERT_API_KEY` is present, it is prioritized; otherwise, the client seamlessly falls back to `GEMINI_API_KEY`.

## Progress & Status
- [x] **`feature_001_domain_models`**: Pure Kotlin domain models and repository interfaces in `:domain` (`accepted`).
- [x] **`feature_002_data_parsing`**: Phase 1 JSON dataset parsing and cached `GameRepositoryImpl` in `:data` (`accepted`).
- [x] **`feature_003_devexpert_client`**: DevExpert & Gemini OpenAI-compatible API client, data sources, and repositories in `:data` (`accepted`).
- [x] **`feature_004_usecases`**: Core business interactors in `:usecases` for game queries, AI tactical insights, and conversational chat (`accepted`).
- [x] **`feature_005_dashboard_ui`**: Tablet Landscape Dashboard UI (80dp NavRail, 64dp TopBar, Hero card, 4 KPI cards, 14-round evolution chart, 14-match table) in `:app` (`accepted`).
- [x] **`feature_006_analysis_ui`**: Tablet Landscape Analysis UI (hero card, 3-filter segmented control, tactical insight cards with metric panels) in `:app` (`not_started`). (accepted)
- [ ] **`feature_007_chat_ui`**: Tablet Single-Thread Chat UI (continuous message history, prompt reflections, AI responses) in `:app` (`not_started`).
- [ ] **`feature_008_dashboard_integration`**: Connect Dashboard to Real Data via `GetPhase1GamesUseCase` and ViewModel in `:app` (`not_started`).
- [ ] **`feature_009_analysis_integration`**: Connect Analysis to Real Data via `GenerateInsightsUseCase` and ViewModel in `:app` (`not_started`).
- [ ] **`feature_010_chat_integration`**: Connect Chat to DevExpert API via `SendChatMessageUseCase` and ViewModel in `:app` (`not_started`).
