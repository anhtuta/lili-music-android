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
| [05 — How to run](05-how-to-run.md) | CLI `make apk` (no Android Studio), SDK setup, Gradle / smoke checks |
| [06 — Synced lyrics](06-synced-lyrics.md) | LRC/XLRC/TRC parser, fetch, Room cache, player pane |
| [07 — Player visuals](07-player-visuals.md) | Settings, lyric themes, Palette, app light/dark |
| [08 — Web parity](08-web-parity.md) | BaiHat.js mismatches vs PR #18; intentional Android extras |
| [09 — Downloads / offline](09-downloads-offline.md) | Media3 downloads, Downloads screen, offline playback |
| [10 — Debug launcher](10-debug-launcher.md) | Blank white `ComponentActivity` on Android 16; MainActivity is the only LAUNCHER |
| [11 — Play Store](11-play-store.md) | Lyric card, App Links, R8/signing, `make smoke` |
| [12 — Lyric wrap / height](12-now-playing-lyrics.md) | Long karaoke wrap, full-height lyric slot, Previous label |
| [13 — Phase 7 web parity](13-phase7-web-parity.md) | Compact lyrics, transport icons, fonts, cancel download, download icons, smooth TRC karaoke, icon buttons + tooltips |

## Status of phases

- [x] [PR #1](https://github.com/anhtuta/lili-music-android/pull/1) — Project init + agent tooling (merged)
- [x] [PR #7](https://github.com/anhtuta/lili-music-android/pull/7) — Kotlin 2.4.21 bump (merged)
- [x] [PR #14](https://github.com/anhtuta/lili-music-android/pull/14) — Data layer + library + package rename (merged)
- [x] [PR #15](https://github.com/anhtuta/lili-music-android/pull/15) — Playback (Media3, queue, listens) (merged to main)
- [x] [PR #16](https://github.com/anhtuta/lili-music-android/pull/16) — Learning guide (merged to main)
- [x] [PR #17](https://github.com/anhtuta/lili-music-android/pull/17) — Synced lyrics (Phase 4) (merged to main)
- [x] [PR #18](https://github.com/anhtuta/lili-music-android/pull/18) — Player visuals / settings (Phase 5) (merged to main)
- [x] [PR #19](https://github.com/anhtuta/lili-music-android/pull/19) — Web parity vs class BaiHat.js (task 010) (merged to main)
- [x] [PR #20](https://github.com/anhtuta/lili-music-android/pull/20) — Downloads / offline (060/061), Makefile (012), debug launcher (013) (merged to main)
- [x] [PR #21](https://github.com/anhtuta/lili-music-android/pull/21) — Lyric card (062), App Links (063), Play packaging (064), release smoke (065), lyric wrap (066), full-height lyrics (067) (merged to main)
- [x] [PR #22](https://github.com/anhtuta/lili-music-android/pull/22) — Phase 7 070–074: compact now-playing, transport icons, lyric-card fonts, cancel download, download action icons (merged to main)
- [ ] This PR — Phase 7 075–076: smooth `.trc` karaoke fill; icon actions with long-press tooltips

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
