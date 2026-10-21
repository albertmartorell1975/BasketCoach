# Project Constraints

* **Strict Tablet Landscape**: The UI must be optimized exclusively for Android Tablet in Landscape mode. No smartphone or portrait-optimized layouts should be considered.
* **Static MVP Dataset**: The MVP relies on a static, bundled dataset (`docs/general/dataset.json`) representing 14 Phase 1 games. No dynamic data entry or remote DB syncing should be built.
* **No Individual Player Stats**: The MVP only models and analyzes team-level and opponent-level statistics.
* **Single Thread Chat**: The chat interface must be a single, continuous thread. No chat history list or multiple sessions.
* **DevExpert API Integration**: All AI insights and chat responses must be routed through the DevExpert API as defined in the discovery.
* **Architecture Constraints**: Follow the `Layer-Based` Clean Architecture topology (`:domain` -> `:usecases` <- `:data` -> `:app`) and `MVVM` with `Hybrid UI State`.
* **Pure Kotlin Domain Mandate**: The `:domain` module must strictly forbid any `android.*` framework dependencies, remaining a 100% pure Kotlin JVM library to preserve testability and architectural boundary separation.
* **Team Identity Constant**: The domain constant `OUR_TEAM_NAME = "BASQUETPIASABADELL BLAU"` is the single authoritative identifier across the domain layer to compute home/away outcomes, scores, and differentials.
