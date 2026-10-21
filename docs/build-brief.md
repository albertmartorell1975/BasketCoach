# Build Brief: BasketCoach AI

## Problem
The coach of the Junior Men's Interterritorial team at CE Pia Sabadell needs a way to analyze past game performances from Phase 1 (14 games) to find team-level synergies, weaknesses, and appropriate practice drills. The team will play a Phase 2 starting in January against different opponents. The overarching objective is to improve the team's performance in Phase 2 compared to Phase 1. Analyzing game data manually is time-consuming and lacks proactive solutions.

## Users
* **Coach (Primary User)**: Uses the app on an Android Tablet to review insights and chat with the AI assistant. 

## Goals
* Provide an Android Tablet application tailored for the coach.
* Use a static dataset of 14 previous Phase 1 games (team vs. opponent stats) to ground AI analysis and prepare for Phase 2. The explicit AI analysis logic is to "find our weakness in game X to avoid repeating it in Phase 2, and strengthen our strength in game Y to repeat and enhance it in Phase 2".
* Display a hybrid interface: a Dashboard with proactive insights/drills, and a single-thread Chat interface for deeper, free-form Q&A.
* Use the DevExpert API to generate intelligent responses and practice solutions based on the team's data.

## Non-Goals
* No individual player statistics (MVP is strictly team-level).
* No dynamic data entry or remote database synchronization (MVP uses bundled static data).
* No complex user authentication or multi-team management.
* No smartphone or portrait-optimized layouts (MVP is strictly constrained to Tablet Landscape mode to reduce complexity).
* No multiple chat threads or chat history list (MVP has only a single continuous chat thread).

## MVP Scope
A single-screen or two-pane landscape tablet application containing:
1. **Dashboard**: Loads the bundled 14-game Phase 1 dataset and prompts the DevExpert API to generate a summary of strengths, weaknesses, and recommended drills aimed at the explicit analysis goal.
2. **Chat Assistant**: A single, continuous conversational UI thread where the coach can ask specific questions (e.g., "Why are we losing rebounds in the 4th quarter?") and the AI answers using the static dataset context.

## Validation & Success Criteria
* The app successfully parses the bundled 14-game Phase 1 dataset.
* The DevExpert API integrates correctly and provides context-aware basketball insights without hallucinating incorrect stats.
* The tablet UI correctly displays both the dashboard and chat interfaces simultaneously or in an easily navigable way.