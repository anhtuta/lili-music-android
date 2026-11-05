# Task 008 — Phase 4: synced lyrics

[Learning guide index](README.md) · Task [008](../tasks/008-synced-lyrics.md)

**What was built.** Pure-Kotlin parsers for `.lrc`, `.xlrc` (`[tranN]` translations), and `.trc` (word durations). `GET /api/lyric?file=` treats HTTP 200 body `Lyric doesn't exist!` as missing. Room caches lyric *text* for offline replay. The now-playing screen shows a synced pane: current line highlighted and auto-scrolled, karaoke fill, countdown dots in long empty gaps, translation lines, session-only ±100ms offset. Tap a line seeks (web does not; Android extra).

**Status:** Merged ([PR #17](https://github.com/anhtuta/lili-music-android/pull/17)).

> [!NOTE]
> **User offset is not saved.** Same as the web player: ±100ms lives in the ViewModel and resets when the song changes. File `[offset:]` still applies. Lyric *themes* / blur / gigantic line / translation toggle landed in [Phase 5](07-player-visuals.md). Media3 downloads of lyric *files* stay Phase 6.

## New terms

| Term | Meaning | Java / web analogy |
| --- | --- | --- |
| `:core:lyrics` | JVM parser + timing math, no Compose | A pure library like a npm `lrc-parser` |
| `LyricParser` | Turns lyric text into `ParsedLyric` | `JSON.parse`, but for `.lrc` / `.trc` |
| `LyricSynchronizer` | Maps player position → current word / fill / dots | The 30ms `setInterval` in web `BaiHat.js` |
| Karaoke fill | Fraction `(now - start) / (end - start)` painted L→R | CSS `linear-gradient` on `background-clip: text` |
| Countdown dots | `•`…`•••••` on empty words in 3s/5s gaps | Web `setBulletText` |
| `GET /api/lyric` | Plain text, not JSON | `axios.get` returning a string |
| `Response<ResponseBody>` | Retrofit raw body when it is not JSON | Reading `InputStream` yourself |
| Room `lyrics` table | Cache of fileName → body | A `Map` persisted in SQLite |
| `feature:lyrics` pane | Compose UI slotted into the player | A React child passed as `children` |
| Session offset | ±100ms in ViewModel memory | A `useState` that is not `localStorage` |

Kotlin `sealed class` (`LyricLoadResult`, `LyricNetworkResult`) is a closed union: `Ready` / `Missing` / `Error`. In Java that is an enum-plus-fields or a visitor; in TypeScript a discriminated union.

## Code to focus on

1. [LyricTimeDecoder.kt](../../core/lyrics/src/main/java/com/anhtuta/lilimusic/core/lyrics/LyricTimeDecoder.kt) — `[mm:ss.xx]` and `[hh:mm:ss.xx]` → milliseconds.
2. [LyricParser.kt](../../core/lyrics/src/main/java/com/anhtuta/lilimusic/core/lyrics/LyricParser.kt) — duplicate timestamps, `[tranN]`, `<ms>word`, meta tags including `offset`.
3. [LyricSynchronizer.kt](../../core/lyrics/src/main/java/com/anhtuta/lilimusic/core/lyrics/LyricSynchronizer.kt) — binary search, overlapping words prefer the later one, karaoke + countdown.
4. [LumenApi.kt](../../core/network/src/main/java/com/anhtuta/lilimusic/core/network/LumenApi.kt) — `GET api/lyric?file=`.
5. [OfflineFirstLyricRepository.kt](../../core/data/src/main/java/com/anhtuta/lilimusic/core/data/repository/OfflineFirstLyricRepository.kt) — network first, Room fallback.
6. [LyricsViewModel.kt](../../feature/lyrics/src/main/java/com/anhtuta/lilimusic/feature/lyrics/LyricsViewModel.kt) — combines `PlayerController.state` with the parsed lyric.
7. [LyricsPane.kt](../../feature/lyrics/src/main/java/com/anhtuta/lilimusic/feature/lyrics/LyricsPane.kt) — list, karaoke `Brush`, offset buttons.
8. [PlayerScreen.kt](../../feature/player/src/main/java/com/anhtuta/lilimusic/feature/player/PlayerScreen.kt) — `lyrics: @Composable () -> Unit` slot so `:feature:player` does not depend on `:feature:lyrics`.
9. [LiliApp.kt](../../app/src/main/java/com/anhtuta/lilimusic/LiliApp.kt) — wires `LyricsRoute` into `PlayerRoute`.

Missing-body sentinel, from `LyricFormat.kt`:

```kotlin
const val MISSING_BODY = "Lyric doesn't exist!"

fun isMissingBody(body: String): Boolean {
    val trimmed = body.trim()
    return trimmed.isEmpty() || trimmed == MISSING_BODY
}
```

The API returns HTTP **200** for a missing file. Do not treat 200 as “lyrics exist.”

Karaoke + countdown, from `LyricSynchronizer.kt`:

```kotlin
fun karaokeFill(startMs: Long, endMs: Long, positionMs: Long, offsetMs: Long): Float {
    val start = startMs + offsetMs
    val end = endMs + offsetMs
    val span = (end - start).toFloat()
    if (span <= 0f) return 1f
    return ((positionMs - start).toFloat() / span).coerceIn(0f, 1f)
}
```

Offset is added at sync time, not baked into parsed timestamps — file `[offset:-300]` plus user ±100ms share one `combinedOffsetMs`.

Why a slot instead of `feature:player` importing `feature:lyrics`? Features must not depend on each other. `:app` is the composition root (like a Spring `@Configuration` or a React router layout that mounts both pages).
