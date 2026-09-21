# Risks and Open Questions

## Blocking Next Phase

* **AI Provider Access**: The initial analysis uses Gemini 3 Flash Preview and the coach chat uses DevExpert Inference `chat-pro`. DevExpert access is temporary and subject to a weekly quota.

* **AI Context & Evidence**: Define what analysis and evidence from `dataset.json` must be passed to `chat-pro` to keep team-specific responses grounded in the available data.

## Implementation-Time Questions

* **Metric Aggregation**: Define the exact aggregation rules for the 14 games and verify them against `dataset.json`.
* **Chat Context**: Define how much analysis and chat history is retained during a session.
* **Chat Lifecycle**: Decide whether the chat persists only during the current session or across app navigation.
* **UI Flow**: Decide between a single-screen Analysis + Chat experience or a two-step navigation flow.

## Later / Not MVP

* Backend / proxy for AI providers.
* Server-side API key handling.
* Multiple datasets or user import.
* Match-by-match or player-level analysis.
* First vs Second Phase comparison.
* Multi-team management, authentication, sharing, export, or cloud sync.

## Assumptions

* `dataset.json` is the only data source and contains the 14 First Phase games.
* The games are analysed as one aggregated team baseline.
* AI features require network connectivity.
* Gemini 3 Flash Preview generates the initial analysis.
* DevExpert Inference `chat-pro` powers the coach chat.
* `DEVEXPERT_API_KEY` is provided via environment configuration and is not committed to the repository.

## Risks

* **API Key Exposure**: Client-side keys can potentially be extracted. Accepted as an MVP limitation.
* **AI Grounding**: AI may introduce unsupported team-specific claims. Mitigate with the Evidence Obligation.
* **Aggregation Errors**: Incorrect calculations would invalidate the team baseline. Mitigate with deterministic logic and tests.
* **Provider / Network Failure**: AI requests may fail or quota may be exhausted. Provide clear error states.
* **Chat Context Growth**: Long conversations may exceed practical context limits. Use bounded history.
* **MVP Scope**: Two AI integrations plus architecture and validation can increase complexity. Keep the vertical slice small.

## Research Tasks

* Define metric aggregation rules.
* Define the structured output of the initial Team Analysis.
* Define the context passed to `chat-pro`.
* Validate quota/error handling and the Analysis → Chat flow.
