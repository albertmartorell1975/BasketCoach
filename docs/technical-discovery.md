# Technical Discovery: BasketCoach

## Product Surface

Android Native Application for Tablets.

## Candidate Stack

* **Language:** Kotlin 2.x.
* **UI:** Jetpack Compose + Material 3.
* **Architecture:** 4-module Clean Architecture.
* **Modules:** `:app`, `:usecases`, `:data`, `:domain`.
* **Dependency Injection:** Hilt.
* **Networking:** Retrofit + OkHttp, with concrete implementations in `:app`.
* **Testing:** JUnit for pure Kotlin modules and Android/Compose testing for application behaviour.

## Data and Storage

* **`dataset.json`**: Pre-loaded application asset containing the raw statistics of the 14 First Phase games.
* The application aggregates the 14 games into the team's **Team State**.
* No user-provided dataset or match import is required.
* Local persistence is not required by the core MVP and remains subject to the chat/analysis lifecycle decisions.

## Integrations

The MVP uses two AI providers with different responsibilities.

### Team Analysis — Gemini 3 Flash Preview

* **Responsibility:** Generate the initial Global Team Analysis from the aggregated 14-game dataset.
* **Integration:** Google Gemini API / Android-compatible client integration.
* **Output:** Structured team-level analysis identifying Strengths, Weaknesses, and Areas for Improvement.

### Coach Chat — DevExpert Inference

* **Base URL:** `https://inference.devexpert.io/v1`
* **Model:** `chat-pro`
* **Endpoint:** `POST /chat/completions`
* **Responsibility:** Interactive coach chat used to explore and refine improvement areas identified by the initial analysis.
* **Context:** The initial analysis and relevant evidence from `dataset.json`.
* **Authentication:** `DEVEXPERT_API_KEY`.
* **Limits:** Handle provider failures, HTTP 429 responses, and weekly usage limits.

## AI Interaction Principles

* Team-specific insights must be traceable to evidence from `dataset.json`.
* The AI must not invent missing team statistics or context.
* Ambiguous coach questions must trigger clarification before a team-specific conclusion.
* The chat follows a reflective, evidence-based coaching approach and can progress from evidence and reflection to specific actions or training ideas.

## Authentication and Authorization

* **MVP:** No user authentication.
* BasketCoach is a standalone local Android application.
* AI provider credentials are supplied through environment/build configuration and are not committed to source control.
* Client-side API keys are accepted as an MVP/prototype limitation.

## Testing and Verification

* **Domain tests:** Deterministic tests for metric aggregation and domain rules.
* **Use case tests:** Tests for analysis and coaching workflows. 
* **Devices** Run the app on an Android tablet device/emulator.
* **Data/App integration tests:** Verification of dataset loading and provider integrations using mocks where appropriate.
* **UI verification:** Compose UI tests and device/emulator validation of the complete MVP flow.
* **AI verification:** Validate evidence grounding, clarification behaviour, and provider error handling.

## Constraints

* **JDK 17** for all modules.
* `:domain`, `:data`, and `:usecases` must remain free of Android dependencies.
* AI provider implementations and Android-specific networking remain in `:app`.
* No backend or cloud application infrastructure in the MVP.
* AI functionality requires network connectivity.
* DevExpert usage is limited by the temporary course access and weekly quota.
* The project should remain within the defined 8-10 week MVP scope.

## Open Technical Decisions

* Exact aggregation rules for every metric in `dataset.json`.
* Exact structure of the Global Team Analysis returned by Gemini.
* Exact evidence/context passed to `chat-pro`.
* Chat history and analysis lifecycle within the MVP.
* Whether local persistence is actually needed once the lifecycle is defined.
* Error and quota handling behaviour for both AI providers.
