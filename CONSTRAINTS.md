# Project Constraints

* **Strict Tablet Landscape**: The UI must be optimized exclusively for Android Tablet in Landscape mode. No smartphone or portrait-optimized layouts should be considered.
* **Static MVP Dataset**: The MVP relies on a static, bundled dataset (`docs/general/dataset.json`) representing 14 Phase 1 games. No dynamic data entry or remote DB syncing should be built.
* **No Individual Player Stats**: The MVP only models and analyzes team-level and opponent-level statistics.
* **Topic-Contextualized Chat Threads**: The chat workbench left panel permanently displays the real Improvement Areas (`InsightType.IMPROVEMENT`) loaded via `GenerateInsightsUseCase` alongside an Open Inquiry option (`CONSULTA LLIURE`). Selecting an area in the left panel keeps the list visible (never replacing it with a static detail view) and switches the right panel to the conversation thread scoped to that selected area.
* **DevExpert API Integration**: All AI insights and chat responses must be routed through the DevExpert API as defined in the discovery.
* **AI & Semantic Routing (JEV & Structured Outputs)**:
  - **JEV Semantic Firewall**: NEVER send raw user input directly to the generative (`chat`) model. ALL incoming chat messages MUST first be evaluated by the `jev` model via the `/v1/decisions` endpoint. If JEV returns `choice == "off_topic"`, ABORT the request immediately and return the localized rejection string (consuming zero generative tokens).
  - **Thread Governance**: System Prompts MUST command the LLM to strictly redirect the user to "Consulta Lliure" using exact localized closing clauses if they deviate from an Improvement Area topic.
  - **Structured Outputs (Tactical Analysis)**: When generating JSON insights, ALWAYS use `temperature = 0.2`. LLMs may wrap JSON in Markdown; ALWAYS pass the raw response through `stripMarkdownCodeFences(raw)` before deserialization. Use `kotlinx.serialization` with a DTO (`AiAnalysisResponseDto`) that explicitly forbids `null` values for mandatory fields.
  - **Zero-Assumption Grounding**: NEVER ask the LLM to guess. ALWAYS prepend empirical Phase 1 context using `PromptTemplates.formatGameSummary(games)`.
  - **Caching (SSOT)**: Tactical analysis MUST be cached in-memory (`AiAnalysisLocalDataSourceImpl`) using a `Mutex` to prevent redundant LLM calls.
* **Architecture Constraints**: Follow the `Layer-Based` Clean Architecture topology (`:domain` -> `:usecases` <- `:data` -> `:app`) and `MVVM` with `Hybrid UI State`.
* **Pure Kotlin Domain Mandate**: The `:domain` module must strictly forbid any `android.*` framework dependencies, remaining a 100% pure Kotlin JVM library to preserve testability and architectural boundary separation.
* **Team Identity Constant**: The domain constant `OUR_TEAM_NAME = "BASQUETPIASABADELL BLAU"` is the single authoritative identifier across the domain layer to compute home/away outcomes, scores, and differentials.
* **No Repository-to-Repository Dependencies**: Repositories MUST NOT inject or depend on other repositories. Any cross-aggregate coordination or contextual enrichment (e.g. passing Phase 1 match context to the AI repository) MUST be orchestrated by Use Cases in the `:usecases` layer.
* **DataSource vs. Repository Error Boundary**: DataSources MUST throw raw technical exceptions (`IOException`, `HttpException`, `SerializationException`, `FileNotFoundException`). Repositories MUST wrap all operations in `safeCall` to translate exceptions into typed `DomainError` results, never leaking framework exceptions to `:domain` or `:usecases`.
* **Main-Safe DataSources**: DataSources performing blocking I/O (file streams, OkHttp calls) MUST manage their own dispatching via `withContext(Dispatchers.IO)`. Repositories and ViewModels must never be burdened with managing I/O dispatchers.
* **Mandatory Network DTOs Without Defaults**: Network DTOs for AI responses MUST model all fields as mandatory, non-nullable types without default values. Corresponding system prompts MUST explicitly forbid omitting keys or returning null values.
* **Configuration Decoupling & Declarative Secret Provisioning**:
  - API keys, base URLs, and model identifiers MUST NOT be hardcoded as inline string literals.
  - In pure Kotlin modules (`:data`), configuration is isolated in provider constants (`DevExpertConfig`, `GeminiConfig`) with fallback resolution (`AiClientConfig.fromEnvironment()`).
  - In the Android application module (`:app`), API keys MUST be provisioned into `BuildConfig` declaratively using Google's official **`secrets-gradle-plugin`** paired with a versioned `local.defaults.properties` file containing non-empty placeholder defaults (`KEY=DEFAULT_KEY`) for CI and new checkouts.
  - Imperative file reading (`java.util.Properties`, `File.inputStream()`) and manual `buildConfigField` string concatenation inside `build.gradle.kts` are strictly FORBIDDEN.
  - DI modules (`NetworkModule`) must resolve runtime keys via `BuildConfig.*` (filtering out placeholder defaults) before falling back to environment variables.
  - **Dual-Provider Fallback Chaining**: `AiClientConfig` models provider failover through `val fallbackConfig: AiClientConfig?` (strictly explicit without default values). When both `DEVEXPERT_API_KEY` and `GEMINI_API_KEY` are provisioned, `NetworkModule` and `AiClientConfig.fromEnvironment()` nest Gemini as the secondary fallback for DevExpert. If `DEVEXPERT_API_KEY` is missing or placeholder, the app boots directly with Gemini. `AiRemoteDataSourceImpl` latches active configuration to the fallback upon HTTP 401, 402, 403, 429, 5xx, or socket timeouts, while excluding offline connectivity errors (`UnknownHostException`, `ConnectException`).
* **Minimum Font Size**: All UI elements (labels, pills, chart axis texts, table cells) MUST use a minimum font size of `12.sp` to ensure high-density legibility and accessibility on tablet displays (never `10.sp` or `11.sp`).
* **Strict Default-Free & Non-Nullable UI Models**: Presentation state models (`*UiState`, `*UiModel`) MUST NOT declare default parameter values or nullable fields. UI Mappers, ViewModels, and preview data factories are the sole authoritative source of state, preventing accidental fallback bugs and enforcing fail-fast state modeling.
* **Zero Hardcoded Strings & Resource Isolation**:
  - ViewModels MUST NOT import `R.string` or construct user-facing error strings. ViewModels expose typed `DomainError` in `content.Error` states.
  - Presentation error translation is handled exclusively in the UI layer using `UiText` (`DynamicString` and `StringResource`) via extension functions (`fun DomainError.toUiText(): UiText`).
  - UI Mappers (`*UiMapper`) MUST NOT pre-concatenate units or language literals (e.g., `"W "`, `"J"`, `" pts"`, `"%"`) or return dummy `0.0` fallbacks. All display labels must be parameterized and formatted in Compose via `stringResource`.
* **Zero Test-Induced Design Damage**: Production signatures of application Composables (such as `BasketCoachApp` or `Navigation`) MUST NEVER be modified, polluted with screen-slot lambdas, or compromised solely to accommodate testing harnesses.
* **Pragmatic High-Value Testing & Anti-Overengineering Rule**:
  - **Pure JVM Presentation Testing (Zero Robolectric UI Tests)**:
    - Tests in `:app` / presentation modules MUST be pure JVM unit tests targeting ViewModels (`*ViewModelTest`) and complex UI mappers (`*UiMapperTest`).
    - Robolectric Compose UI tests (`*ScreenTest`, `NavigationTest`) are strictly forbidden; they introduce slow build times, flaky text matching, and test-induced design damage without providing true visual layout verification.
    - UI rendering and state correctness are verified via mandatory `@Preview` (`@BcThemePreview` / `@BcDevicePreview`) coverage and `./gradlew lintDebug`.
  - **Strict Test Budget (Hard Caps)**:
    1. **Per Test Class**: Write **1 to 3 cohesive `@Test` methods maximum per test class**.
    2. **Per Feature Slice**: Plan and implement at most **2 to 6 new `@Test` methods in total across all layers** for a standard feature slice.
  - **Single-Layer Assertion Rule (Zero Cross-Layer Redundancy)**:
    - Each behavior, business rule, or corner case MUST be tested **ONCE at the lowest layer that owns the logic**, and NEVER re-tested at higher layers.
  - **Zero Trivial Tests ("Watermelon Tests")**: Strictly FORBID writing tests for:
    1. Kotlin `data class` constructors, getter/setter assignments, or default field instantiation.
    2. Static preview data (`*PreviewData`) or hardcoded navigation option lists.
    3. 1:1 pass-through UseCases that merely delegate directly to a single repository method with zero conditional logic, transformation, or orchestration.
    4. 1:1 mappers that copy identical properties without transformation or business rules.
    5. In-memory data sources that merely wrap standard library collections.
    6. Compose UI components via Robolectric.
  - **High-Value Focus**: Tests MUST strictly focus on:
    1. Domain logic, mathematical calculations, differentials, rankings, and boundary edge cases.
    2. Exception and error mapping (HTTP 4xx/5xx, timeouts, technical exceptions to typed `DomainError`).
    3. Multi-source orchestration and graceful fallbacks in UseCases.
    4. Complex data transformations / JSON serialization and deserialization edge cases.
    5. ViewModel passive lifecycle, state transitions (`Loading`, `Error(DomainError)`, `Success`), and retry recovery.
