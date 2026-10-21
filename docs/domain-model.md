# Domain Model

## Core Entities

### GameData
Represents a single match from the static dataset (primarily Phase 1).
* **Attributes**: Game ID, Phase (e.g., Phase 1), Date/Round, Opponent Name, Team Stats, Opponent Stats, Result.

### TeamStats
Aggregated performance metrics for a team in a game.
* **Attributes**: Points, Rebounds, Assists, Turnovers, Shooting Percentages, Fouls, etc.

### Insight
An AI-generated conclusion based on GameData.
* **Attributes**: Title, Description, Related Games, Category (Offense/Defense/Synergy).

### Drill
A practice exercise recommended by the AI.
* **Attributes**: Name, Objective, Instructions, Focus Area.

### ChatMessage
A message within the AI assistant conversation.
* **Attributes**: Sender (Coach/AI), Text, Timestamp.

## Lifecycle Rules
1. **Data Initialization**: On app startup, the bundled dataset (JSON/CSV) is parsed into `GameData` entities in memory.
2. **Dashboard Generation**: The app sends an initial aggregate of `GameData` to the DevExpert API to generate initial `Insight` and `Drill` lists.
3. **Chat Session**: The coach initiates a message. The app appends the message to the conversation history, includes relevant `GameData` context, and requests a completion from the DevExpert API. The response is parsed as an AI `ChatMessage`.