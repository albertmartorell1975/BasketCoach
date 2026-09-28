# Feature Implementation Spec: Load and parse 14-game statistical dataset from asset JSON

## Source Feature

- `id`: `f01_dataset_loader`
- `area`: `Data`
- `depends_on`: `[]`
- `status`: `not_started`
- `source`: `feature_list.json`

## Goal

Provide the core data loading capability to parse the 14 First Phase basketball games from `dataset.json` stored as an asset. This feature maps raw JSON data into domain entities (`Game`, `FinalScore`, `TeamGameStats`) and exposes a data source interface in `:data` / `:domain` so downstream features (such as `f02_team_state_aggregator`) can consume structured game statistics with 100% mathematical fidelity.

## Non-Goals

- Calculating team aggregate metrics or mathematical averages across games (handled in `f02_team_state_aggregator`).
- Building UI screens, ViewModels, or Jetpack Compose components (handled in `f06_dashboard_screen` and `f07_analysis_screen`).
- Integrating Google Gemini or DevExpert AI clients (handled in `f05_gemini_analysis_integration` and `f08_devexpert_chat_client`).
- Dynamic remote JSON fetching or database persistence (out of MVP scope).

## Job Story

When the application initializes or requests First Phase baseline data,
I want to load and parse the raw `dataset.json` asset into strongly-typed domain models,
so I can access accurate game statistics for all 14 First Phase games.

## Users And Permissions

- **Head Coach (End User)**: Implicit beneficiary. The coach accesses team analysis built on top of this verified raw dataset. No authentication or user roles are needed for asset data loading.

## Acceptance Scenarios

### Scenario 1: Successfully load and parse 14 games from JSON asset

Given the `dataset.json` file containing 14 First Phase game statistics is located in the assets folder
When the dataset loader reads and parses the asset file
Then exactly 14 `Game` domain models are returned, containing correct game metadata (round, date, arena, home/away teams), `FinalScore`, `homeStats`, and `awayStats`.

### Scenario 2: Statistical accuracy verification for individual games

Given the parsed dataset containing 14 games
When inspecting Round 1 (`BASQUETPIASABADELL BLAU` vs `CB. CORNELLÀ`)
Then `home_points` is 62, `away_points` is 78, `offensive_rebounds` for home team is 5, `turnovers` is 19, and `fg3_pct` is 21.4.

### Scenario 3: Missing or corrupted asset handling

Given an unreadable or corrupted JSON asset stream
When the dataset loader attempts to parse the dataset
Then a typed `DatasetParseException` is thrown with an informative message rather than failing silently.

## Repository Research

### Files Inspected

- `docs/general/dataset.json` — Source JSON containing 14 First Phase games data.
- `domain/build.gradle.kts` — Pure Kotlin `:domain` module definition.
- `data/build.gradle.kts` — `:data` module definition depending on `:domain`.
- `app/build.gradle.kts` — `:app` module definition depending on `:domain`, `:data`, and `:usecases`.
- `gradle/libs.versions.toml` — Version catalog for dependencies.

### Existing Patterns To Follow

- 4-module Clean Architecture (`:app`, `:usecases`, `:data`, `:domain`).
- Pure Kotlin domain entities in `:domain`.
- Data sources and serialization logic in `:data`.

### Current Gaps

- `:domain` lacks data models for games, scores, and team statistics.
- `:data` lacks JSON serialization library (e.g. `kotlinx-serialization-json` or Moshi) and asset reading helper.
- `:app/src/main/assets/dataset.json` asset directory does not yet contain `dataset.json`.

## Technical Approach

1. **Domain Layer (`:domain`):**
   - Create pure Kotlin data classes:
     - `Game`: `round: Int`, `date: String`, `time: String`, `city: String`, `arena: String`, `address: String`, `homeTeam: String`, `awayTeam: String`, `finalScore: FinalScore`, `homeStats: TeamGameStats`, `awayStats: TeamGameStats`.
     - `FinalScore`: `homePoints: Int`, `awayPoints: Int`.
     - `TeamGameStats`: `ftPct: Double`, `fg2Pct: Double`, `fg3Pct: Double`, `efgPct: Double`, `offensiveRebounds: Int`, `defensiveRebounds: Int`, `totalRebounds: Int`, `turnovers: Int`, `assists: Int`, `astToRatio: Double`, `steals: Int`, `blocks: Int`, `secondChancePoints: Int`, `fastBreakPoints: Int`, `personalFouls: Int`.
   - Create domain repository/data source interface: `DatasetDataSource` with suspend or synchronous `fun getFirstPhaseGames(): List<Game>`.

2. **Data Layer (`:data`):**
   - Add `kotlinx-serialization-json` to `gradle/libs.versions.toml` and `data/build.gradle.kts` (or Moshi/org.json for zero-dependency parsing).
   - Create Data Transfer Objects (DTOs) with `@Serializable` annotations matching JSON field names (`round`, `home_team`, `away_team`, `final_score`, `home_stats`, `away_stats`).
   - Create `AssetDatasetDataSource` implementing `DatasetDataSource`, which reads JSON string from InputStream/Asset and parses it into domain entities via DTO mappers.

3. **Assets & App Layer (`:app`):**
   - Copy `docs/general/dataset.json` to `app/src/main/assets/dataset.json` (and `data/src/main/resources/dataset.json` or test resources for JVM unit testing).

4. **Unit Tests:**
   - Create JVM unit test in `:data` / `:app` that executes `AssetDatasetDataSourceTest`, loading `dataset.json` and asserting:
     - Total count == 14 games.
     - Round 1 and Round 14 data match `dataset.json` exactly.

## Expected File Changes

- `gradle/libs.versions.toml` — modify; add `kotlinx-serialization` dependency / plugin.
- `data/build.gradle.kts` — modify; apply serialization plugin and dependencies.
- `domain/src/main/java/com/martorell/albert/basketcoach/domain/model/Game.kt` — create; domain entity.
- `domain/src/main/java/com/martorell/albert/basketcoach/domain/model/FinalScore.kt` — create; domain entity.
- `domain/src/main/java/com/martorell/albert/basketcoach/domain/model/TeamGameStats.kt` — create; domain entity.
- `domain/src/main/java/com/martorell/albert/basketcoach/domain/datasource/DatasetDataSource.kt` — create; domain interface.
- `data/src/main/java/com/martorell/albert/basketcoach/data/dto/GameDto.kt` — create; DTOs with serialization mappings.
- `data/src/main/java/com/martorell/albert/basketcoach/data/datasource/AssetDatasetDataSource.kt` — create; JSON loader implementation.
- `app/src/main/assets/dataset.json` — create; asset copy of raw data.
- `data/src/test/java/com/martorell/albert/basketcoach/data/AssetDatasetDataSourceTest.kt` — create; unit test verifying dataset parsing.

## Visual Design Impact

- UI involved: no
- Design source: not applicable
- Screens or states affected: none
- New design artifact required: no

## Durable Documentation Impact

- `ARCHITECTURE.md`: create — document the 4 Clean Architecture modules (`:app`, `:usecases`, `:data`, `:domain`) and data flow boundaries for dataset access.
- `CONSTRAINTS.md`: update/not needed — not needed.
- `AGENTS.md`: update/not needed — not needed.

## Implementation Plan

1. Define domain entities (`Game`, `FinalScore`, `TeamGameStats`) and `DatasetDataSource` contract in `:domain`.
2. Add serialization dependencies in `gradle/libs.versions.toml` and `:data/build.gradle.kts`.
3. Create DTOs (`GameDto`, `FinalScoreDto`, `TeamGameStatsDto`) and mapping extensions in `:data`.
4. Copy `dataset.json` to `:app/src/main/assets/dataset.json` and `:data/src/test/resources/dataset.json`.
5. Implement `AssetDatasetDataSource` in `:data`.
6. Write unit test `AssetDatasetDataSourceTest` and verify all 14 games load with 100% statistical accuracy.

## Implementation Tasks

- [ ] Create domain models (`Game`, `FinalScore`, `TeamGameStats`) in `:domain`.
- [ ] Create `DatasetDataSource` interface in `:domain`.
- [ ] Configure `kotlinx.serialization` in `libs.versions.toml` and `data/build.gradle.kts`.
- [ ] Create DTO classes and mapping extension functions in `:data`.
- [ ] Implement `AssetDatasetDataSource` in `:data`.
- [ ] Place `dataset.json` in assets and test resources.
- [ ] Write and run `AssetDatasetDataSourceTest` unit test.

## Verification Plan

- `./gradlew :data:test`
- `./gradlew :app:testDebugUnitTest`
- `./init.sh`
- Expected result: Build succeeds and unit tests pass, verifying 14 games are parsed accurately from `dataset.json`.

## Evidence To Capture

- Test execution output showing `AssetDatasetDataSourceTest` passed with 14 games loaded.
- Command log of `./init.sh` passing.

## Validator Checklist

- [ ] Implementation stays within `f01_dataset_loader` scope.
- [ ] All 14 games are loaded without data truncation or precision loss.
- [ ] Clean Architecture module boundaries are strictly respected (`:domain` has zero Android or data dependencies).
- [ ] `AssetDatasetDataSourceTest` passes cleanly.
- [ ] `feature_list.json` status updated to `passing` with exact evidence.
