---
name: hilt
description: Expert guidelines for using Dagger Hilt in Android projects.
metadata:
  author: Albert Martorell Garcia
  version: 1.1.0
---
# Hilt Specialist

Guidelines for clean architecture boundaries and DI optimizations.

## Layer Boundaries
* `:domain`: Agnostic of DI.
* `:usecases`: Constructor injection only.
* `:data`: Modules with `@Binds` or `@Provides`.
* `:app`: Central orchestrator (`@HiltAndroidApp`).

## Optimization Rules
* Isolation of `@Binds` (abstract) and `@Provides` (object).
* Use `@param:ApplicationContext` for Kotlin 2.x compatibility.
* Multibinding patterns with `@JvmSuppressWildcards`.
