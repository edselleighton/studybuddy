---
description: Run the unit test suite and report failures and untested new code
---

Run `./mvnw -B verify -DskipITs` (integration tests need Docker and run only in CI).

Then report:
1. Whether the build passed, and for any failure the test name and the key lines of the error.
2. Any new or changed production code under `src/main/java` that has no corresponding test, so it can be covered.
