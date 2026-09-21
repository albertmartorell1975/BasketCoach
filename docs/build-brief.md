# Build Brief: BasketCoach
Android tablet application.

## Problem

Head coaches of **Junior teams at Club Esportiu Bàsquet Pia de Sabadell** have access to game statistics, but transforming those statistics into actionable training focuses and coaching strategies is a manual process.

BasketCoach uses the statistics from the team's **First Phase (14 games)** to create a team baseline, identify areas for improvement, and help the coach explore possible solutions for the **Second Phase**.

## Current Workaround / Existing System

The MVP starts from a predefined dataset containing the statistics of the team's 14 First Phase games.

There is currently no automated process that aggregates these statistics into a team baseline and connects the detected improvement areas with practical training focuses for the Second Phase.

## Target User

* **Head Coach (Junior Team)**: Primary user. Generates the team baseline and explores tactical and training improvements through the AI chat.

## Goals

* Load and aggregate 14 games of statistical data from a predefined `dataset.json`.
* Generate a **Global Team Analysis** identifying Strengths, Weaknesses, and Areas for Improvement.
* Use the First Phase statistics as the team's baseline.
* Provide an AI Chat interface for coaches to ask follow-up questions and explore and refine specific areas for improvement through reflective, evidence-based coaching dialogue.
* Transform the analysis and the coach's questions into concrete **Training Focuses** for the Second Phase.
* Ensure every team-specific insight or claim is traceable to specific evidence contained in `dataset.json`.
* Apply the **Zero Assumption Rule**: when a coach's question is ambiguous or there is insufficient team-specific evidence, the AI must ask for clarification rather than inventing context or conclusions.
* Keep the application client-side, with direct connectivity to the AI providers and no backend or cloud application infrastructure. AI features require network connectivity.
* Follow a strict 4-module Clean Architecture.

## Non-Goals

* Real-time game tracking or live stats entry.
* User-provided JSON import.
* Match-by-match analysis.
* Multi-team management or user profiles/login.
* Management of individual player profiles.
* Exporting or sharing analysis.
* Backend or Cloud Sync implementation.

## MVP Slice

A complete vertical flow:

1. One-tap **"Generate Analysis"** from the pre-loaded 14-game dataset.
2. Display of a structured **Global Team Analysis**.
3. Active AI Chat session using `chat-pro` to explore and refine the analysis and specific improvement areas through reflective, evidence-based coaching dialogue.
4. Generate actionable training focuses for the Second Phase.

```text
                    FIRST PHASE
                     14 games
                         │
                         ▼
                   dataset.json
                         │
                         ▼
                ┌─────────────────┐
                │ Generate        │
                │ Analysis        │
                └────────┬────────┘
                         │
                         ▼
                ┌─────────────────┐
                │ GLOBAL TEAM     │
                │ ANALYSIS        │
                │    BASELINE     │
                └────────┬────────┘
                         │
                 Strengths / Weaknesses
                 Areas for Improvement
                         │
                         ▼
                ┌─────────────────┐
                │    AI CHAT      │
                │ Coach questions │
                └────────┬────────┘
                         │
                         ▼
              Specific actions / training ideas
                         │
                         ▼
                 SECOND PHASE
              Training Focuses
```

## AI Strategy

The MVP uses two AI integrations with different responsibilities:

* **Gemini 3 Flash Preview**: Generates the initial Global Team Analysis from the aggregated 14-game dataset.
* **DevExpert Inference / `chat-pro`**: Provides the interactive AI Chat used by the coach to ask follow-up questions, explore specific improvement areas, and refine them into specific actions or training ideas.

The chat should use the team's initial analysis and available evidence as context. It should follow the project's reflective, evidence-based coaching approach rather than immediately providing generic tactical solutions.

Conceptually:

```text
                         BASKETCOACH
                              │
                     ┌────────┴────────┐
                     │                 │
              Generate Analysis     Coach Chat
                     │                 │
                     ▼                 ▼
                 :usecases          :usecases
                     │                 │
                     ▼                 ▼
                   :data             :data
                 contracts          contracts
                     │                 │
                     └────────┬────────┘
                              │
                             :app
                     ┌────────┴─────────┐
                     │                  │
                     ▼                  ▼
              Gemini 3 Flash       DevExpert
                 Preview            chat-pro
                     │                  │
                     ▼                  ▼
              Team Analysis       Chat Response
```

## Architecture

The project already defines a strict 4-module architecture governed by the project's skills:

* `:app`
* `:usecases`
* `:data`
* `:domain`

The architecture follows Clean Architecture, separation of responsibilities, SOLID principles, and the module boundaries defined by the project's governance skills.

The AI providers must fit into this existing architecture and must not introduce a separate AI-specific architecture.

## Validation Plan

* Run the app on an Android tablet device/emulator and complete the flow: Data Loading → Analysis Generation → Coaching Chat.

* Verify that the mathematical aggregation of the 14 games matches the raw data in `dataset.json`.

* Verify that the initial analysis uses the aggregated metrics from the provided `dataset.json`.

* Verify that the analysis is team-level rather than match-by-match.

* Test the chatbot with follow-up tactical questions, for example:

    * "How can we improve our half-court defense?"
    * "How can we improve defensive transition?"
    * "How can we improve offensive rebounding?"

* Verify that every team-specific insight or claim can be traced to evidence in `dataset.json`.

* Verify that ambiguous questions trigger clarification before a team-specific conclusion is generated.

* Verify that the chat helps the coach explore improvement areas and refine them into specific actions or training ideas.

## Success Criteria

* A coach can generate a Global Team Analysis based on the 14 First Phase games.
* The team baseline accurately represents the aggregated statistics of the 14 First Phase games.
* The analysis identifies relevant Strengths, Weaknesses, and Areas for Improvement.
* Every team-specific insight or claim is grounded in evidence available in `dataset.json`.
* The coach can use the AI Chat to ask specific follow-up questions.
* The chatbot helps the coach explore identified improvement areas and refine them into actionable recommendations or training ideas.
* Ambiguous questions are clarified before the AI produces team-specific conclusions.
* The team baseline can be used to define training focuses for the Second Phase.
* The app maintains the intended Clean Architecture boundaries between Domain, Data, Use Cases, App, and the AI providers.

## Security / API Keys

* DevExpert access uses the `DEVEXPERT_API_KEY` environment variable.
* The API key must not be committed to the repository or written directly into source code or configuration.
* For the MVP, the AI providers are accessed directly from the Android client.
* This client-side API key approach is accepted as an MVP/prototype limitation; a future production implementation could move provider calls behind a backend or proxy.

## Development Scope

* **Expected development time:** 8-10 weeks.
* The project remains an MVP and does not aim to implement production-scale backend infrastructure or multi-user functionality.

## Notes

* **Documentation Language:** English.
* **Interface Language:** Spanish and Catalan.
* **AI Stack:** Google Gemini and DevExpert Inference via direct client-side integration.