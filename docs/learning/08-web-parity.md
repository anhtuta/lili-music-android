# Task 010 — Web player parity

[Learning guide index](README.md) · Task [010](../tasks/010-web-parity-fixes.md)

**What was built.** A stacked pass on Phase 5 / PR #18 so Android matches the class-component web player (`BaiHat.js`, `Player.js`, `BaiHatTable.js` from `liliana-player-reactjs`). Authoritative route is `App.js` `/bai-hat` → class `BaiHat.js`, not `BaiHatFunctional.js`.

**Status:** This PR (base `cursor/phase-5-player-visuals-cca4`; retarget to `main` after #18 merges).

Changed to match web: previous always skips track (no 3s restart); listen timer only from position 0 and cancelled only on `path` change; queue is the full catalog (search filters the view); ±5s seek; first-track previous no-op + toast copy; unknown duration 600s; trust API order; list row fields + cover preview; gigantic line default on; lyric meta footer; tappable Mail/FB; countdown only if a next word exists; 30ms lyric tick; album under artist.

## Intentional differences from the web app

Kept on purpose (also in [PLAN.md §10](../PLAN.md)):

| Item | Android | Web |
| --- | --- | --- |
| **4** | Speed 0.5–2.0× | No that clamp |
| **7 / 21** | Restore last song paused | No restore |
| **11** | Extra asc/desc toggle (API `sortOrder`) | Fixed order per column click |
| **12** | Instant Compose search | `filterTable` on the DOM |
| **13** | Tap lyric line to seek | No seek from lyrics |
| **20** | Cover Palette on now-playing | Average RGB only on waveform |
| **22** | External App Links deferred to PLAN **063** | `#/bai-hat/{path}` |

## New terms

| Term | Meaning | Java / web analogy |
| --- | --- | --- |
| `SkipPreviousPolicy` | Pure previous-button decision | `Player.js` `prevSong` reducer |
| `ListenCountPolicy` | When to arm / cancel the 60s PUT | `onPlay` `setTimeout` + path check |
| `PlaybackSeek.INCREMENT_MS` | 5000ms back/forward | `currentTime ± 5` |
| `PlaybackDuration.UNKNOWN_FALLBACK_MS` | 600s when Media3 has no length | `getDuration()` Infinity → 600 |
| `catalogIndex` | Room column for API array order | The `songData` array index |
| `SongOfTheYearPalette` | Rainbow badge colours | `BaiHatTable.js` `rainbowColors` |
| `LinkAnnotation.Url` | Tappable span inside `Text` | `<a href="mailto:…">` |
| `LyricMetaFooter` | Title/creator/source/date under lyrics | `appendLyricMeta` |
| `C.TIME_UNSET` | Media3 “duration unknown” | HTML5 `audio.duration === Infinity` |
| App Links (deferred) | HTTPS `intent-filter` verified by Play | Same URL the web already has |

WorkManager unique work is still the listen timer: one pending PUT per app, replaced when `path` changes. Pause does **not** `cancelUniqueWork`.

`catalogIndex` is needed because SQLite `INTEGER PRIMARY KEY` **is** `rowid`, so `SELECT * FROM songs` would otherwise come back in `id` order, not API order.

## Code to focus on

1. [SkipPreviousPolicy.kt](../../core/playback/src/main/java/com/anhtuta/lilimusic/core/playback/SkipPreviousPolicy.kt) / [RepeatAwarePlayer.kt](../../core/playback/src/main/java/com/anhtuta/lilimusic/core/playback/RepeatAwarePlayer.kt) — notification Previous must not use Media3’s 3s restart.
2. [ListenCountPolicy.kt](../../core/playback/src/main/java/com/anhtuta/lilimusic/core/playback/ListenCountPolicy.kt) — `positionMs == 0L`; cancel only on path change.
3. [LibraryViewModel.kt](../../feature/library/src/main/java/com/anhtuta/lilimusic/feature/library/LibraryViewModel.kt) — `playableCatalog` vs filtered `songs`; sort change re-fetches.
4. [SongDao.kt](../../core/database/src/main/java/com/anhtuta/lilimusic/core/database/SongDao.kt) — `ORDER BY catalogIndex`.
5. [LibrarySongRow.kt](../../feature/library/src/main/java/com/anhtuta/lilimusic/feature/library/LibrarySongRow.kt) — listens / SOY / type / `formatCreatedDate` / cover dialog.
6. [LyricMetaFooter.kt](../../core/lyrics/src/main/java/com/anhtuta/lilimusic/core/lyrics/LyricMetaFooter.kt) / [LyricsPane.kt](../../feature/lyrics/src/main/java/com/anhtuta/lilimusic/feature/lyrics/LyricsPane.kt) — footer + Mail/FB links.
7. [LyricSynchronizer.kt](../../core/lyrics/src/main/java/com/anhtuta/lilimusic/core/lyrics/LyricSynchronizer.kt) — countdown requires `globalIndex < lastIndex`.
8. [PlayerScreen.kt](../../feature/player/src/main/java/com/anhtuta/lilimusic/feature/player/PlayerScreen.kt) — ±5s, first-song copy, `(No Album)`.

Listen policy after 010:

```kotlin
val startedFromStart =
    currentPlaying &&
        !currentPath.isNullOrBlank() &&
        positionMs == 0L &&
        (!previousPlaying || pathChanged)
val cancelPending = pathChanged && previousPath != null
```

Previous on the first track (not repeat-one) keeps `currentPosition` and sets `PlaybackState.atStartOfQueue` so the player can show the web toast string.
