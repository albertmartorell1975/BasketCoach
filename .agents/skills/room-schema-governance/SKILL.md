---
name: room-schema-governance
description: Ensures database integrity and schema evolution rules for Room.
metadata:
  author: Albert Martorell Garcia
  version: 1.0.0
---
# room-schema-governance Specialist

Enforces integrity rules for Room persistence.

## Core Rules
1. **Auto Version Increment**: Increment `version` if entities change.
2. **Migration Protocol**: Use `fallbackToDestructiveMigration` in dev, manual migrations in prod.
3. **Verification**: Run assemble to trigger processor and verify schema export.
