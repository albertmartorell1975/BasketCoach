# Basket Coach
An AI-assisted coaching tool that transforms structured team statistics into evidence-based analysis, coaching questions and actionable training focuses.

## Architecture

BasketCoach follows a **Layer-Based Clean Architecture** pattern across four primary modules:
- **`:domain`**: Core business entities (`Game`, `TeamStats`, `Score`, `Venue`, `Insight`, `Drill`), typed errors (`DomainError`), and repository contracts (`GameRepository`, `AiAnalysisRepository`, `ChatRepository`). Pure Kotlin JVM with zero external dependencies.
- **`:usecases`**: Domain interactors orchestrating analytical and conversational workflows.
- **`:data`**: Repository implementations, JSON dataset parser (`kotlinx.serialization`), in-memory caching, and future DevExpert AI client.
- **`:app`**: Tablet Landscape Jetpack Compose UI (two-pane dashboard and single-thread chat assistant) using Hilt and MVVM Hybrid UI State.

## Progress & Status
- [x] **`feature_001_domain_models`**: Pure Kotlin domain models and repository interfaces in `:domain` (`accepted`).
- [x] **`feature_002_data_parsing`**: Phase 1 JSON dataset parsing and cached `GameRepositoryImpl` in `:data` (`accepted`).
- [ ] **`feature_003_devexpert_client`**: DevExpert API client for insights and chat (`next`).
