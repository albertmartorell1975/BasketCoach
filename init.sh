#!/usr/bin/env bash
set -euo pipefail

echo "Repository: $(pwd)"
echo "Checking environment..."
java -version

echo "Running baseline verification..."
./gradlew testDebugUnitTest --quiet

echo "=== Baseline verification completed successfully ==="
