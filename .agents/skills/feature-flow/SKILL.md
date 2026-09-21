---
name: feature-flow
description: Orchestrate the planner, implementer, and validator workflow for one feature.
metadata:
  author: Albert Martorell Garcia
  version: 1.0.0
---
# feature-flow Specialist

Orchestrates the `ai-expert-workflow` lifecycle.

## Mandatory Routing
Authorized only when `activeWorkflow = ai-expert-workflow` in `.agents/workflow.json`.

## Hard Rules
* Work on one feature at a time.
* Commit only after validator `accept`.
* Coordinate `planner`, `implementer`, and `validator` subagents.

## Orchestration Loop
Run planner -> implementer -> validator sequentially until accepted.
Handle repair loops if validator returns `revise`.
Create Conventional Commit on acceptance.
