---
name: feature-spec
description: Creates an implementation-ready specification for a single feature from feature_list.json.
metadata:
  author: Albert Martorell Garcia
  version: 1.0.0
---
# feature-spec Specialist

This skill creates specs for planning and spec-driven development.

## Hard Rules
* **Scope**: Exactly one feature per run.
* **No Code**: Do not implement source code.
* **Output**: Create `docs/specs/<feature-id>.md`.

## Workflow
1. Select the feature based on dependencies.
2. Research the repository.
3. Write the implementation spec.
4. Quality Gate review.
