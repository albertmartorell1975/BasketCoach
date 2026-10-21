# Design Direction

## Source of Truth
* **Wireframes**: The user will provide custom wireframes and designs later. Those assets will act as the visual source of truth.
* **Base System**: Material 3 (Jetpack Compose).
* **Target Surface**: Android Tablet.

## Visual Identity
* **Theme**: Colors and typography should align with the Club Esportiu Pia Sabadell visual identity (to be confirmed from provided designs).

## Android Resource Conversion Policy
When implementing features that require visual assets:
1. Full-screen layout mockups provided by the user belong in `docs/features/<feature-id>/ui/screens/` as reference (NEVER converted to drawables).
2. Graphic assets (logos, avatars, icons) belong in `docs/features/<feature-id>/ui/resources/`.
3. Graphic assets MUST be converted to optimized `.webp` format in the target UI module:
   * Layer-Based: `app/src/main/res/drawable/ic_<name>.webp`
   * Feature-First: `<feature>/presentation/src/main/res/drawable/ic_<name>.webp` or `core/designsystem/src/main/res/drawable/`

## Layout Principles
* **Tablet Landscape Only**: Utilize the larger screen real estate, strictly constrained to Landscape mode.
* **Mockup Adaptation**: We must follow the existing `docs/ui/` mockups but adapt them to the MVP constraints: favor two-pane landscape layouts exclusively and skip the chat list (as the app features only a single continuous chat thread).