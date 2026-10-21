# Domain Model

## Core Concepts

* **Game Stats**: Statistics recorded for a single game from the First Phase.

* **Baseline Phase**: The 14 First Phase games used as the reference period for the team's performance.

* **Team State**: The aggregated statistical state of the team calculated from the Baseline Phase.

* **Coaching Analysis**: The interpretation of the Team State that identifies team Strengths, Weaknesses, and Areas for Improvement.

* **Evidence**: Statistical data from the Baseline Phase that supports a team-specific insight in the Coaching Analysis.

* **Chat Session**: An interaction in which the Head Coach explores and refines an improvement area using the AI.

* **Training Focus**: A specific area of work derived from an improvement area and the subsequent coaching exploration, intended for the Second Phase.

## Relationships

```text
Game Stats (14)
      │
      ▼
Baseline Phase
      │
      ▼
Team State
      │
      ▼
Coaching Analysis
      │
      ├── Evidence
      │
      ▼
Chat Session
      │
      ▼
Training Focus
```

## States and Lifecycles

* **Coaching Analysis**: `Pending` → `Generating` → `Ready`
* **Chat Session**: `Idle` → `Active`
* **Training Focus**: `Identified` → `Refined`

## Important Scenarios

1. **Initial Analysis**: The 14 First Phase games are aggregated into the Team State, which is used to generate the Coaching Analysis.

2. **Coaching Exploration**: The Coach selects an improvement area and asks a question in the Chat Session. The AI uses the analysis and available evidence to help explore and refine the issue.

3. **Training Focus**: The coaching exploration leads to a specific Training Focus for the Second Phase.

## Edge Cases

* Invalid or incomplete game data.
* Aggregated Team State does not match the underlying 14 games.
* Insufficient evidence for a team-specific insight.
* Ambiguous coaching question.
* AI response not supported by the available evidence.
