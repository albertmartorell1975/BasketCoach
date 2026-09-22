# Instructions for Agents

## Project Overview

This repository contains a **tablet-only Android MVP for Junior basketball coaching** at Club Esportiu Bàsquet Pia Sabadell. It uses the aggregated statistics from 14 First Phase games to generate an evidence-based team analysis and help coaches refine improvement areas into training focuses for the Second Phase.

## Read First

* `CONTEXT.md` — project purpose, domain language, AI interaction principles, and verification rules.
* `docs/build-brief.md` — MVP problem, goals, scope, and core user flow.
* `docs/domain-model.md` — domain concepts, relationships, states, and scenarios.
* `docs/risks-and-open-questions.md` — risks, assumptions, and open questions.

Read additional documentation only when relevant:

* `docs/technical-discovery.md` — when working on stack, integrations, AI, data, or technical decisions.
* `DESIGN.md` — when working on UI, styles, or visual components.
* `docs/prompt.md` — when working on AI prompts or coaching behaviour.
* `docs/dataset.json` — when working on the dataset or statistical processing.
* `.agents/rules.md` — when following the project prompt/task workflow.
* `.agents/skills/` — when a task is covered by a project governance or implementation skill.

## Startup Workflow

Before writing code:

1. Check the current branch with `git branch --show-current`.
2. Read `PROGRESS.md` and `feature_list.json` when present/active.
3. Read the relevant documentation and skills for the task.
4. Verify the current project state before making changes.

## Working Rules

* Work on one feature or task at a time.
* Keep changes within the defined scope.
* Follow the project's documented architecture and governance skills.
* Apply **KISS**: prefer the simplest solution that satisfies the MVP requirement.
* Do not invent product requirements, domain data, or unsupported team insights.
* Keep durable project state in repository files rather than relying on chat history.

## Required Artifacts

* `feature_list.json` — feature status, when used by the active workflow.
* `PROGRESS.md` — verified project progress and session state.

## Definition of Done

A feature is complete only when:

* The target behaviour is implemented.
* Required verification has been run successfully.
* Relevant project state is updated.
* Relevant documentation is updated when behaviour, domain rules, APIs, or verification changes.

## End of Session

Before ending a session:

1. Update `PROGRESS.md` and `feature_list.json` when required.
2. Record relevant risks or unresolved blockers.
3. Verify the final project state.
