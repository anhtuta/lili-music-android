# Phase 12 — Now Playing refinement (112–117)

## What we built

1. **Overflow app bar (112).** Download, Lyric card, and Settings moved into a dropdown from a smaller gear (`NowPlayingOverflowMenu`).
2. **Edge-to-edge lyrics (113).** Removed the rounded `LyricSlot` clip; lyric background spans full width while line padding stays at 16.dp.
3. **Control inset (114).** `navigationBarsPadding()` plus 12.dp under the seek/transport row.
4. **Lyric font sizes (115).** Five persisted presets: Compact, Normal (default), Comfortable, Large, Extra large (`LyricFontSize` → `PlayerSettings.lyricFontSize`).
5. **Listen count fix (117).** `ListenCountPolicy` tolerates a few ms of position after a **path change** so WorkManager still schedules `RecordListenWorker` when ExoPlayer’s first frame is not exactly 0.

## Terms (Java / web)

| Kotlin / Android | Meaning | Java / web analogy |
| --- | --- | --- |
| `DropdownMenu` | Popup anchored to a button | `<select>` or headless UI menu |
| `navigationBarsPadding()` | Insets for gesture/nav bar | `env(safe-area-inset-bottom)` |
| `LyricFontSize` enum + DataStore | Persisted UI scale | CSS `font-size` preset in `localStorage` |
| `ListenCountPolicy.START_TOLERANCE_MS` | Accept near-zero start on new track | Web `onPlay` when `currentTime === 0` but ExoPlayer reports 1–50ms |

## Code to focus on

1. [NowPlayingOverflowMenu.kt](../../feature/player/src/main/java/com/anhtuta/lilimusic/feature/player/NowPlayingOverflowMenu.kt)
2. [PlayerScreen.kt](../../feature/player/src/main/java/com/anhtuta/lilimusic/feature/player/PlayerScreen.kt) — `PlayerChromeFooter`, full-width `LyricSlot`
3. [LyricFontSize.kt](../../core/model/src/main/java/com/anhtuta/lilimusic/core/model/LyricFontSize.kt) + [LyricFontMetrics.kt](../../feature/lyrics/src/main/java/com/anhtuta/lilimusic/feature/lyrics/LyricFontMetrics.kt)
4. [ListenCountPolicy.kt](../../core/playback/src/main/java/com/anhtuta/lilimusic/core/playback/ListenCountPolicy.kt)
