# 008 — Synced lyrics engine and player pane

## Goal

While a song plays, the now-playing screen shows synced lyrics that track the Media3 position: current line highlighted and auto-scrolled, word-level karaoke fill for `.trc`, countdown dots in long empty gaps, `.xlrc` translation lines, and a session-only ±100ms sync control. Lyrics come from `GET /api/lyric?file=` and are parsed in pure Kotlin.

## Approach

Implements PLAN Phase 4 (040–046) plus the Phase 5 slice needed to *see* lyrics on the player (050: highlight, autoscroll, karaoke, countdown, translations). Settings-driven visuals (alignment, gigantic line, word/line appear, blur, lyric themes), cover Palette, and the settings screen stay Phase 5 (051–053). Downloads stay Phase 6.

Behaviour is copied from the recovered web player (`anhtuta/lili-player` source maps: `BaiHat.js`, `BaiHatService.js`, `UtilService.decodeTime`). Private `liliana-player-reactjs` remains unreadable.

- **Fetch.** `GET /api/lyric?file=` (URL-encoded). HTTP 200 body `Lyric doesn't exist!` (or empty `Song.lyric`) → missing lyric contribution copy (mail / FB from web `NO_LYRIC`).
- **Formats.** `.lrc` timed lines + duplicate timestamps on one line; `.xlrc` is `.lrc` plus `[tranN]` bound to the preceding timed line; `.trc` line timestamp plus `<ms>word` (blank words are countdown slots). Meta: `offset`, `by` / deprecated `author`, `date` / deprecated `createdDate`, `source`, `ti`, `ar`. File `offset` is milliseconds added at sync time, not baked into parsed times. `.xtrc` is out of scope.
- **Sync.** Combined offset = file offset + user ±100ms. User offset is **session-only** (web does not persist it; resets on song change). Karaoke fill `(now - start) / (end - start)` default on. Countdown: empty word/line, if gap ≥5s show dots in the last 5s, else if gap ≥3s show dots in the last 3s (`•`…`•••••`, `Math.ceil(timeLeft)`).
- **Tap a line to seek.** Web does **not** seek on lyric tap (`div_result` click only closes settings). Android **does** seek to the line’s audio time (start + combined offset) as a small UX extra; noted as a deviation.
- **Cache.** Room table of lyric *bodies* keyed by file name: network first, fall back to cache when offline. This is replay/offline reuse of text, not Media3 file downloads (those stay 060).
- **Modules.** `:core:lyrics` stays JVM / Compose-free. `:feature:lyrics` owns the pane + ViewModel. `:feature:player` does not depend on `:feature:lyrics`; `:app` slots the pane into `PlayerScreen`.

### Non-goals

Downloads, sleep timer, settings screen, lyric themes / blur / gigantic line / word-appear / line-appear toggles, lyric card export, HTTPS App Links, waveform / `RECORD_AUDIO`, Zing streaming, admin/login, changing hello_lumen.

## Files

- `docs/tasks/008-synced-lyrics.md` (this file)
- `docs/PLAN.md` (Phase 4 progress)
- `core/model/` — `ParsedLyric`, `LyricFormat`, `LyricMeta`, `LyricLine`, `LyricWord`
- `core/lyrics/` — `decodeTime`, LRC/XLRC/TRC parser, `LyricSynchronizer` (karaoke + countdown)
- `core/network/` — `GET /api/lyric?file=` as plain text
- `core/database/` — `LyricEntity` / `LyricDao`, schema v2 migration
- `core/data/` — `LyricRepository` (fetch, missing-body, Room cache, parse)
- `core/playback/` — position ticker ~50ms while playing (karaoke)
- `feature/lyrics/` — `LyricsViewModel`, `LyricsPane`, strings, tests
- `feature/player/` — optional lyrics slot on `PlayerScreen` (no feature→feature dependency)
- `app/` — compose `LyricsPane` into the player route

## Tests

- **decodeTime (JVM):** `[mm:ss.xx]`, `[mm:ss.xxx]`, `[hh:mm:ss.xx]`, invalid → 0.
- **LRC fixtures:** meta tags (incl. `[offset:]`, deprecated `author`/`createdDate`), duplicate timestamps expanded then sorted, empty lines as countdown slots, last-line end = duration, unknown tags ignored, BOM/CRLF.
- **XLRC fixtures:** `[tran1]`/`[tran2]` bound to preceding timed line; multi-timestamp line binds translation to the last stamp (web).
- **TRC fixtures:** word durations, empty `<n>` slots, skip lines without `<ms>` words, offset + ar/ti/by/date.
- **Synchronizer (JVM):** current word, overlapping times prefer the later word, karaoke fill 0 / mid / 1, countdown 5s vs 3s rules, combined file+user offset, no lyric.
- **Network (MockWebServer):** encoded Unicode `file`; 200 + `Lyric doesn't exist!` → missing; 200 text/plain body → text.
- **Room:** upsert/get lyric body; migration 1→2 keeps songs.
- **Repository (fakes):** missing vs body vs cache fallback on network error.
- **LyricsViewModel (Turbine + FakePlayerController):** load on song change; highlight follows `advancePosition`; ±100ms; reset offset on song change; tap line seeks.
- **Compose (Robolectric):** missing copy shown; line tap; offset buttons; translation text visible.

No emulator. Parser tests use checked-in fixtures under `core/lyrics/src/test/resources/lyrics/`.

## Commit breakdown

1. `docs(tasks): add 008 synced lyrics plan` — this file only.
2. `feat(model): add parsed lyric domain types`
3. `feat(lyrics): parse lrc, xlrc, and trc with karaoke timing`
4. `feat(network): fetch lyric text and treat missing body as empty`
5. `feat(database): cache lyric bodies in Room`
6. `feat(data): add lyric repository with cache fallback`
7. `feat(playback): tick position at 50ms for karaoke`
8. `feat(lyrics): add synced lyrics pane and ViewModel`
9. `feat(player): slot lyrics into the now-playing screen`
10. `feat(app): host lyrics pane on the player route`
11. `docs: mark Phase 4 lyrics progress in PLAN`
12. `docs(learning): add synced lyrics guide` — alone, after code.

Implementation commits include tests for that layer.

## Done when

- Task plan committed **before** code (this file).
- `.lrc` / `.xlrc` / `.trc` parse and sync match the web player (offsets, karaoke, countdown, translations).
- Player shows lyrics; missing lyric shows the contribution message; no Zing streaming; no `RECORD_AUDIO`.
- User ±100ms is session-only; file offset applied; tap-to-seek documented as an Android extra.
- Lyric bodies cached in Room for offline replay; Media3 downloads still deferred.
- Learning-guide page appended in its own commit.
- `./gradlew check` passes.
