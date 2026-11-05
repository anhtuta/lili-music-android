# Lili Music Android — Learning guide

For Tú: what each merged phase built, Android/Kotlin terms mapped to Java and web ideas, and the exact files worth reading. Append a section after every future phase.

> [!NOTE]
> **How to use this.** You know Java and React; you are new to Android and Kotlin. Each page has the same shape: what we built, a terms table with Java/web analogies, then repo files to read. Future phases append the same way.

## Pages

| Page | What it covers |
| --- | --- |
| [00 — Big picture](00-overview.md) | Modules, song flow, architecture diagram |
| [01 — PR #1 Init](01-project-init.md) | Multi-module shell, Hilt, CI, agent tooling |
| [02 — PR #7 Kotlin](02-kotlin-bump.md) | Kotlin 2.4.21 + Gradle / KSP / compileSdk alignment |
| [03 — PR #14 Data](03-data-and-library.md) | Song model, Retrofit, Room, library UI |
| [04 — PR #15 Play](04-playback.md) | Media3, queue, listen counting, player UI |
| [05 — How to run](05-how-to-run.md) | Android Studio, Gradle commands, smoke checks |
| [06 — Synced lyrics](06-synced-lyrics.md) | LRC/XLRC/TRC parser, fetch, Room cache, player pane |

## Status of phases

- [x] [PR #1](https://github.com/anhtuta/lili-music-android/pull/1) — Project init + agent tooling (merged)
- [x] [PR #7](https://github.com/anhtuta/lili-music-android/pull/7) — Kotlin 2.4.21 bump (merged)
- [x] [PR #14](https://github.com/anhtuta/lili-music-android/pull/14) — Data layer + library + package rename (merged)
- [x] [PR #15](https://github.com/anhtuta/lili-music-android/pull/15) — Playback (Media3, queue, listens) (merged to main)
- [x] [PR #16](https://github.com/anhtuta/lili-music-android/pull/16) — Learning guide (merged to main)
- [ ] This PR — lyrics engine (Phase 4) (task 008)

## After a later phase

Add `docs/learning/NN-short-name.md` with the same three parts:

1. What was built
2. New Android/Kotlin terms with Java/web analogies
3. Code to focus on (paths + short excerpts)

Link it from this index, tick the status list, and commit that update **alone**. See [AGENTS.md](../../AGENTS.md).

## Also read

- [docs/PLAN.md](../PLAN.md) — v1 scope, API contract, phases
- [AGENTS.md](../../AGENTS.md) — workflow, modules, commands
- [README.md](../../README.md) — stack and build commands
