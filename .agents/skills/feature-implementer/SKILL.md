---
name: feature-implementer
description: Implements exactly one planned feature from a feature spec file.
metadata:
  author: Albert Martorell Garcia
  version: 1.0.0
---
# feature-implementer Specialist

This skill implements a feature, self-verifies, and updates progress.

## Role Boundary
* **Do**: Implement the spec, self-verify, update status.
* **Do Not**: Broaden scope or declare final acceptance.
* **Status**: Set to `passing`.

## Workflow
1. Confirm the contract.
2. Set state to `in_progress`.
3. Smallest coherent implementation.
4. Self-verify (tests, static checks).
5. Update harness state and record evidence.
