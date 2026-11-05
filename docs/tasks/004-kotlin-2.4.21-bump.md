# 004 — Resolve Kotlin 2.4.21 Dependabot bump

> Copy this file to `docs/tasks/NNN-short-name.md`, fill every section, and **commit it before writing code**. See [AGENTS.md](../../AGENTS.md) and [docs/PLAN.md](../PLAN.md).

## Goal

PR [#7](https://github.com/anhtuta/lili-music-android/pull/7) (`dependabot/gradle/kotlin-2.4.21`) can merge into current `main` with Kotlin 2.4.21 and a green `./gradlew check` / CI run.

## Approach

Work on the existing Dependabot branch. Do not open a new PR.

1. Merge `origin/main` into `dependabot/gradle/kotlin-2.4.21` (merge, not rebase).
2. Resolve conflicts by keeping `main`'s already-merged catalog and CI bumps, plus `kotlin = "2.4.21"`.
3. If the compiler bump requires aligned plugin versions (KSP, Compose compiler plugin, kotlinx-serialization plugin, Hilt), update only those pins in `gradle/libs.versions.toml` in a separate commit.
4. Compose and serialization Gradle plugins already `version.ref = "kotlin"`; they move with the Kotlin pin. Hilt 2.60.1 is already on `main`.

Non-goals: Phase 1 feature work, new screens, backend changes, opening a replacement PR.

## Files

- `docs/tasks/004-kotlin-2.4.21-bump.md` (this plan)
- `gradle/libs.versions.toml` (conflict resolution + any aligned pins)

## Tests

No new production behaviour. Existing JVM unit tests plus `./gradlew check` (lint, ktlint, detekt) must pass on Kotlin 2.4.21. Add a focused test only if the bump forces a code change.

## Done when

- `origin/main` is merged into `dependabot/gradle/kotlin-2.4.21`
- Catalog keeps `main`'s pins and `kotlin = "2.4.21"`
- Companion versions required by Kotlin 2.4.21 are aligned
- `./gradlew check` passes
- PR #7 CI is green
