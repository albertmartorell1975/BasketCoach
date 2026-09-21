---
name: harness-starter
description: Creates or updates the minimal startup harness for a software repository after product discovery but before implementation.
metadata:
  author: Albert Martorell Garcia
  version: 1.0.0
---
# harness-starter Specialist

This skill prepares the repository for a coding agent by defining what the project is and how to start it.

## Hard Rules
1. **Strict Scope**: Only create/update `AGENTS.md`, `init.sh`, `PROGRESS.md`, and `feature_list.json`.
2. **No Implementation**: Do not implement product code.
3. **Required Inputs**: Must read discovery docs (`CONTEXT.md`, `build-brief.md`, etc.).

## Workflow
* Slice features into "session-sized" units.
* Create a machine-readable `feature_list.json`.
* Ensure `init.sh` is executable.
