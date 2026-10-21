# BasketCoach Architecture

## 1. Overview & System Topology

BasketCoach is structured around a **Layer-Based Clean Architecture** topology across four primary modules:

```mermaid
flowchart TD
    app[":app (UI & Presentation)"]
    usecases[":usecases (Interactors / Business Logic)"]
    data[":data (Repositories & Data Sources)"]
    domain[":domain (Entities & Contracts)"]

    app --> usecases
    app --> data
    usecases --> domain
    data --> domain
```

### Module Responsibilities & Dependencies

| Module | Purpose | Allowed Dependencies | Platform Target |
| :--- | :--- | :--- | :--- |
| **`:domain`** | Core basketball domain entities, value objects, typed errors (`DomainError`), and repository contracts. | None (pure Kotlin JVM, zero external/Android dependencies). | Pure Kotlin JVM (`libs.plugins.kotlin.jvm`) |
| **`:usecases`** | Single-responsibility domain interactors orchestrating business workflows. | `:domain` | Pure Kotlin JVM / Android |
| **`:data`** | Repository implementations, local data parsing (`dataset.json`), database persistence (`Room`), and remote AI clients (`DevExpert API`). | `:domain` | Android / JVM |
| **`:app`** | Tablet Landscape Jetpack Compose UI, MVVM ViewModels with Hybrid UI State, and Hilt dependency injection root. | `:domain`, `:usecases`, `:data` | Android Application |

---

## 2. Domain Layer Governance (`:domain`)

The `:domain` module is the innermost layer and embodies the core business rules of basketball analytics:

- **Strict Pure Kotlin Mandate**:
  Zero Android framework dependencies (`android.*`). All types, collections, and primitives are pure Kotlin (`kotlin.*`).
- **Immutable State Representation**:
  Entities and value objects are modeled as immutable Kotlin `data class` types with `val` properties.
- **Single Source of Domain Truth**:
  Constants such as `OUR_TEAM_NAME = "BASQUETPIASABADELL BLAU"` reside directly in `:domain`, enabling dynamic resolution of home/away perspective, score differentials, and win/loss records.
- **Typed Functional Error Modeling**:
  Repository contracts return `Result<T, DomainError>` sealed hierarchies rather than throwing raw runtime exceptions, preventing technology leaks from data sources.

---

## 3. Core Domain Entities & Taxonomy

```mermaid
classDiagram
    direction TB
    class Game {
        +Int round
        +String date
        +String time
        +Venue venue
        +String homeTeam
        +String awayTeam
        +Score finalScore
        +TeamStats homeStats
        +TeamStats awayStats
        +Boolean isHomeGame
        +Int teamScore
        +Int opponentScore
        +Boolean isWin
        +String opponentName
        +TeamStats teamStats
        +TeamStats opponentStats
        +Int pointDifferential
    }

    class TeamStats {
        +Double ftPct
        +Double fg2Pct
        +Double fg3Pct
        +Double efgPct
        +Int offensiveRebounds
        +Int defensiveRebounds
        +Int totalRebounds
        +Int turnovers
        +Int assists
        +Double astToRatio
        +Int steals
        +Int blocks
        +Int secondChancePoints
        +Int fastBreakPoints
        +Int personalFouls
        +Int points
    }

    class Score {
        +Int homePoints
        +Int awayPoints
    }

    class Venue {
        +String city
        +String arena
        +String address
    }

    class Insight {
        +String id
        +String title
        +String description
        +InsightCategory category
        +List~Int~ relatedGameRounds
    }

    class Drill {
        +String id
        +String name
        +String objective
        +List~String~ instructions
        +String focusArea
    }

    class ChatMessage {
        +String id
        +MessageSender sender
        +String text
        +Long timestamp
        +Boolean isFromCoach
        +Boolean isFromAi
    }

    Game --> Score
    Game --> Venue
    Game --> TeamStats
```

### Domain Repositories (Contracts in `:domain`)
- **`GameRepository`**: Access to historical Phase 1 game fixtures and team box scores.
- **`AiAnalysisRepository`**: Querying tactical AI insights and recommended practice drills.
- **`ChatRepository`**: Single-thread conversational assistant interaction with the DevExpert AI service.
