# Design Direction

## Overview

BasketCoach should feel like a **simple, clean, focused basketball coaching tool**.

The interface should make statistics easy to understand, AI insights easy to follow, and the relationship between **data, evidence, and coaching actions** clear.

## KISS Principle

**Keep It Simple, Stupid (KISS).**

This is an MVP. Prefer the simplest design that clearly communicates the product's value.

Avoid unnecessary screens, components, interactions, animations, and visual complexity.

## Existing Design Assets

The visual identity follows **Club Esportiu Bàsquet Pia Sabadell**.

No additional brand assets are required for the MVP.

## Product Feel

* Simple and clean.
* Focused on basketball data and coaching.
* Professional but appropriate for a Junior coaching environment.
* Strong hierarchy for statistics and key insights.
* Pia Blue for primary actions.
* Tactical Orange used selectively for improvement areas.

## Colors

* **Primary — Pia Blue:** `#1D2E54`
* **Secondary — Pia White:** `#FFFFFF`
* **Accent — Tactical Orange:** `#F15A24`
* **Background:** `#F5F5F5`
* **Surface:** `#FFFFFF`
* **Text:** `#1A1A1A`
* **Text Muted:** `#616161`
* **Success:** `#008C45`
* **Danger:** `#D32F2F`

Orange should be used selectively to highlight improvement areas, not as a dominant colour.

## Typography

* **Primary font:** Roboto Flex.
* Use clear weight hierarchy for headings and key statistics.
* AI-generated content should remain highly legible.
* Statistics should be easy to scan.

## Tablet Layout

The MVP targets **Android tablets only**.

Support:

* Tablet portrait.
* Tablet landscape.

Use the available tablet space to improve readability and organisation, rather than simply scaling a phone layout.

Prefer simple layouts with clear sections and comfortable spacing.

## Components

Use standard Material 3 components wherever possible.

* **Primary Button:** Pia Blue (`#1D2E54`), used for **"Generate Analysis"**.
* **Metric Card:** Displays an important team statistic.
* **Analysis Card:** Displays an insight together with its supporting evidence.
* **Improvement Indicator:** Tactical Orange (`#F15A24`) for areas requiring attention.
* **Strength Indicator:** Success Green (`#008C45`) for positive findings.
* **Chat Interface:** Clearly distinguishes Coach and AI messages.
* **Loading / Error States:** Simple and clear feedback for AI operations.

Avoid creating custom components unless they provide clear MVP value.

## Core Screens

Keep the MVP to three main screens:

1. **Dashboard:** First Phase baseline and **"Generate Analysis"** action.
2. **Analysis:** Global Team Analysis with Strengths, Weaknesses, and Areas for Improvement.
3. **Coaching Chat:** Exploration and refinement of identified improvement areas.

## Interaction Principles

* **Evidence Proximity:** Team-specific AI claims should appear close to the evidence supporting them.
* **Data Clarity:** Important statistics should be understandable at a glance.
* **Direct Access:** The **"Generate Analysis"** action should be easy to find.
* **Reflective Coaching:** Chat should support exploration and refinement rather than generic tactical advice.
* **Clear Roles:** Coach and AI messages must be visually distinguishable.
* **Actionable Outcome:** Coaching exploration should help turn improvement areas into practical Training Focuses.
* **Minimal Interaction:** Prefer straightforward flows with as few steps as possible.

## Accessibility

The UI follows the project's **Design System Governance** rules for accessibility, adaptive behaviour, reusable components, typography, and visual verification.

## Do's and Don'ts

* **Do:** prioritize clarity, statistics, evidence, and coaching actions.
* **Do:** use standard Material 3 components where possible.
* **Do:** use the tablet space to improve readability.
* **Don't:** build a complex dashboard.
* **Don't:** add charts or interactions unless they provide clear MVP value.
* **Don't:** add decorative elements without a functional purpose.
* **Don't:** make AI content visually more important than the underlying evidence.
* **Don't:** add screens or navigation flows without a clear user need.

## Open Design Questions

* Final navigation pattern between Dashboard, Analysis, and Chat.
* Whether Chat should be a full-screen destination or a simple secondary presentation.
* Whether charts add enough value to justify their implementation in the MVP.
