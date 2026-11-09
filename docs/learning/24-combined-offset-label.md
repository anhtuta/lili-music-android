# 24 — Combined lyric offset label (task 137)

## What changed

The lyric offset overlay (tap pane → minus/plus) now shows **total** offset ms: file `[offset:…]` from the lyric meta plus any client ±100ms nudges. `LyricSynchronizer` already used that sum for sync (`combinedOffsetMs`); only the label previously showed `userOffsetMs` alone.

## Terms

| Kotlin / Compose | Idea | Web analog |
| --- | --- | --- |
| `LyricMeta.offsetMs` | Parsed from `[offset:N]` in `.lrc` / `.xlrc` / `.trc` | Lyric meta tag baked into timing |
| `userOffsetMs` | Session-only nudge stored in `LyricsViewModel` | `offsetTime` in `BaiHat.js` |
| `combinedOffsetMs` | `meta.offsetMs + userOffsetMs` on `LyricSyncFrame` | Effective shift applied to word times |

## Files to read

1. [LyricsPane.kt](../../feature/lyrics/src/main/java/com/anhtuta/lilimusic/feature/lyrics/LyricsPane.kt) — `OffsetRow` reads `state.frame.combinedOffsetMs`.
2. [LyricSynchronizer.kt](../../core/lyrics/src/main/java/com/anhtuta/lilimusic/core/lyrics/LyricSynchronizer.kt) — defines `combinedOffsetMs`.
3. [LyricsViewModel.kt](../../feature/lyrics/src/main/java/com/anhtuta/lilimusic/feature/lyrics/LyricsViewModel.kt) — still owns `userOffsetMs`; re-syncs frame on each nudge.
