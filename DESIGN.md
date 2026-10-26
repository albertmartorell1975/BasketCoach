# Design Direction & Visual Identity — BasketCoach

## 1. Source of Truth
* **Primary Visual Reference**: The design sketches and HTML prototypes located under `docs/ui/`.
* **Base System**: Material 3 (Jetpack Compose).
* **Target Surface**: Android Tablet (1280x800dp), strictly constrained to Landscape mode (`sensorLandscape`).

## 2. Pia Sabadell Design System Palette
Authoritative brand tokens from `docs/ui/basquet_pia_design_system.jpeg`:
* **Primary (Navy)**: `#1D2E54` — Used for Navigation Rail background, prominent titles, and "Explorar al Xat" action buttons.
* **Secondary (White)**: `#FFFFFF` — Top bar background, card containers, active navigation rail item background.
* **Accent (Tactical Orange)**: `#F15A24` — Critical indicators, improvement area card borders, alerts, highlights.
* **Background**: `#F5F5F5` — General application scaffold canvas.
* **Surface**: `#FFFFFF` — Cards, panels, dialog surfaces.
* **Text Primary**: `#1A1A1A` — Headings, titles, high-emphasis metrics.
* **Text Muted**: `#616161` — Subtitles, context tags, secondary labels.
* **Feedback - Success**: `#2E7D32` (accent badges `#008C45`) — Wins, positive tactical strength chips, verified indicators.
* **Feedback - Danger**: `#D32F2F` — Losses, negative score differentials, critical alerts.

## 3. UI Screens & Feature Mapping Index
Every feature touching presentation MUST consult its designated sketches under `docs/ui/screens/`:

| Feature ID | Feature Title | Sketch References | Composable Scope |
| :--- | :--- | :--- | :--- |
| `feature_005_dashboard_ui` | Tablet Landscape Dashboard & Anàlisi UI | `docs/ui/screens/dashboard_vertical/`<br>`docs/ui/screens/analisis_landscape/` | Fixed Navy `BasketCoachNavRail` (80dp), White `BasketCoachTopBar` (64dp), `DashboardScreen` (Hero card, 4 KPI cards, 14-round Bar Chart, 14-match statistical table), and `AnalysisScreen` (Hero card, segmented filter control, 3-column `InsightCard` grid with "Explorar al Xat"). |
| `feature_006_chat_ui` | Tablet Single-Thread Chat UI | `docs/ui/screens/chat_general/`<br>`docs/ui/screens/chat_detalle/` | Single continuous thread interface, message bubbles (coach vs assistant), tactical reflection area selector, and socratic drill recommendation suggestions. |

## 4. Android Resource Conversion Policy
When implementing features that require visual assets:
1. Full-screen layout mockups provided by the user belong in `docs/features/<feature-id>/ui/screens/` as reference (NEVER converted to drawables).
2. Graphic assets (logos, avatars, icons) belong in `docs/features/<feature-id>/ui/resources/`.
3. Graphic assets MUST be converted to optimized `.webp` format in the target UI module:
   * Layer-Based: `app/src/main/res/drawable/ic_<name>.webp`
   * Feature-First: `<feature>/presentation/src/main/res/drawable/ic_<name>.webp` or `core/designsystem/src/main/res/drawable/`

## 5. Layout Principles
* **Tablet Landscape Only**: High-density 1280x800dp landscape layout. Navigation Rail on the left (80dp), content canvas spanning remaining width.
* **Language & Localization**: All user-facing strings are localized in **Catalan** (`res/values/strings.xml`), reflecting the local basketball club context (CE Escola Pia Sabadell).
