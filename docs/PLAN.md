# Lili Music Android — Overall Plan

Native Android listener app that replicates the web player at [lili-player](https://github.com/anhtuta/lili-player) (built deploy of the private `liliana-player-reactjs` source). Backend is [hello_lumen](https://github.com/anhtuta/hello_lumen) (PHP Lumen) at `https://blog.nenmongvietnam.com/laravel/public`. **The backend must stay unchanged.**

This document is the source of truth for v1 scope, architecture, phases, and risks. Each implementation task gets its own plan in `docs/tasks/` (see `docs/tasks/TEMPLATE.md`) and is committed **before** code.

## 1. Product scope (v1)

Play Store release, **listener only**: no admin, no login, no waveform.

| In scope | Out of scope |
| --- | --- |
| Play only songs with a non-empty `file_name` via `GET /api/song/file?file=` | Admin UI, auth, login |
| Hide/skip Zing songs (`zing_id` streaming is dead) | Waveform / `RECORD_AUDIO` |
| Library list: sort + local search | Uploading / editing songs |
| Player: sequence / shuffle (current song first) / repeat-one, speed, notification + lock screen | Zing MP3 streaming |
| Synced lyrics: `.lrc`, `.xlrc` (`[tranN]`), `.trc` (word-level); karaoke fill; countdown dots; meta tags including `offset`; ±100ms user offset | `.xtrc` (web notes “future”) |
| Settings: lyric themes, alignment, blur, gigantic line, word/line appear, show translation, speed, light/dark app theme | Flip waveform |
| Cover-art colour background | |
| Lyric card export/share | |
| Offline downloads (audio + lyric + cover) | |
| Deep links to `bai-hat/{path}` | |

### Verified against live API and web player source maps

Private repos `anhtuta/liliana-player-reactjs` and `anhtuta/hello_lumen` were not readable from this environment. Behaviour was verified against:

- Live backend `https://blog.nenmongvietnam.com/laravel/public`
- Public built web player [anhtuta/lili-player](https://github.com/anhtuta/lili-player) (original sources recovered from JS source maps)

Re-check the private sources when access is available; do not silently change API contracts.

## 2. Backend contract (do not change)

Base URL: `https://blog.nenmongvietnam.com/laravel/public`

All listed endpoints are public (`Access-Control-Allow-Origin: *`). No auth required for listener flows.

| Method | Path | Notes |
| --- | --- | --- |
| `GET` | `/api/song/all?sortBy=&sortOrder=` | Unpaged JSON **array** of songs. Live count ~152. Default `sortBy=title`, `sortOrder=ASC`. Invalid `sortBy` → HTTP **500** — send only the whitelist below. `page` / `per_page` / combined `sort=` are ignored (`sortBy` wins). |
| `GET` | `/api/song/file?file=` | Audio (`audio/mp3`). Supports `Accept-Ranges: bytes`. Missing file → **404** JSON `Song doesn't exist!`. |
| `GET` | `/api/lyric?file=` | Plain text when present (`text/plain`). Missing: HTTP **200**, body `Lyric doesn't exist!`, `Content-Type: text/html`. `file` may be repeated; first existing file wins. |
| `PUT` | `/api/song/listens?path=` | Count a listen. Web fires this **60s after play starts from position 0**, only if still on the same song `path`. Legacy `?file=` still works; Android should use `path`. |
| `GET` | `/api/song/picture?file=` | Cover image (used when `image_url` is a relative picture path). |
| `GET` | `/api/song/album?file=` | Alternate cover path still present on some `image_url`s; resolve whatever relative URL the song has. |

Do **not** call `/api/zing/mp3/streaming`. `GET /api/song/type/all` is public but unused in v1 (type is already on each song). Admin/auth routes stay unused.

### Song JSON fields (live)

`id`, `title`, `artist`, `album`, `path`, `type`, `listens`, `file_name`, `image_name`, `image_url`, `lyric`, `zing_id`, `song_of_the_year`, `created_date`, `formatCreatedDate`, plus `updated_at` (present live; treat as optional).

Playable filter: `file_name` is a non-empty string. Zing-only songs have `file_name: null` and a `zing_id`; **hide them entirely**.

`image_url` may be:

- Absolute (`https://…`, including leftover Zing CDNs)
- Relative (`/api/song/picture?file=…` or `/api/song/album?file=…`) — prefix the API base URL
- `null` — use a placeholder

### Sort (from web `BaiHatTable.js`)

| Column | `sortBy` | Default `sortOrder` |
| --- | --- | --- |
| Song | `title` | `ASC` |
| Listens | `listens` | `DESC` |
| Song of the year | `song_of_the_year` | `DESC` |
| Type | `type` | `ASC` |
| Date | `createdDate` | `DESC` |

`createdDate` and `created_date` both work. Persist last sort in DataStore. Never send an unknown `sortBy`.

Local search is client-side substring match on title/artist (web filters the Song column).

The **web** prefers `zing_id` over `file_name` when both exist. **Android v1 does the opposite of that streaming path:** hide any song without a non-empty `file_name`, including Zing-only rows.

### Audio URL

`{BASE}/api/song/file?file={file_name}` (URL-encode `file`). Do **not** use `/api/zing/mp3/streaming`.

## 3. Behaviour to match (web)

Sources: `pages/BaiHat/BaiHat.js`, `Player.js`, `Setting.js`, `BaiHatService.js`, `UtilService.js`, `constants/Constants.js`, lyric-card components.

### Playback modes (`playType`)

`SEQUENCE` | `SHUFFLE` | `REPEAT_ONE` (mutually exclusive; shuffle and repeat-one each toggle back to sequence).

Shuffle: Fisher–Yates the playlist, then **move the current song to index 0**. Leaving shuffle restores the original list and relocates the current song.

End of list in sequence/shuffle: stop and warn (web does not wrap). Repeat-one: next/prev replay the same song.

Speed: ±0.1×, persisted. Volume persisted on web; Android uses system/media volume plus optional in-app volume if we keep it — default to Media3 session volume.

MediaSession: title, artist, artwork, play/pause/stop, next/prev. Map this to Media3 `MediaSessionService` (notification + lock screen).

### Lyrics

Formats:

- `.lrc` — line timestamps `[mm:ss.xx]` (also `[hh:mm:ss.xx]`). Duplicate timestamps on one line duplicate the line at each time.
- `.xlrc` — `.lrc` plus `[tranN]…` translation lines bound to the preceding timed line.
- `.trc` — line timestamp plus word durations `<ms>word`. Empty words (`<n>` with blank text) are countdown slots.

Meta tags: `offset`, `by` / deprecated `author`, `date` / deprecated `createdDate`, `source`, `ti`, `ar`. File `offset` is milliseconds added to all times. User offset is ±100ms per tap, client-only, **not persisted** on web (session only); Android should match unless we later persist it.

Countdown dots: on empty words, if gap ≥5s show dots in the last 5s, or if gap ≥3s show dots in the last 3s (`•`…`•••••` from `setBulletText`).

Karaoke: fill the current word left-to-right as `(now - start) / (end - start)`. Web default is **on** (`karaoke-mode` on the lyric container). Light fill `#2199F9`, dark fill `#FC0`.

Tick interval on web is 30ms; Android can use a ~16–50ms frame loop tied to player position.

Missing lyric body equals `Lyric doesn't exist!` or empty `lyric` field → show the “no lyric yet” contribution message (mail / FB from web `NO_LYRIC`).

### Settings (persisted in web `localStorage` key `LILIANA_SETTINGS`)

Lyric playground: `lyricAlign` (left/center/right), lyric theme `dark` | `light` | `picture-bg`, `karaokeMode`, `blurMode`, `giganticLine`, `wordAppear`, `lineAppear`, `translate`, `speed`.

Web defaults when a key is missing: `playType` = `SEQUENCE`, `speed` = `1.0`, `karaokeMode` = **true**, lyric theme = **`picture-bg`**, `appTheme` = **light**, `blurMode` / `giganticLine` / `wordAppear` / `lineAppear` / `translate` = off. User lyric offset is session-only.

App chrome theme: `appTheme` light/dark (`SETTING_APP_THEME`). Separate from the lyric playground theme.

Skip `showWaveform`, `flipWaveform`, `fullscreen` as product requirements. Do not request `RECORD_AUDIO`.

### Cover colour

Web `getAverageRGB` (brightness 1.6) tints **waveform bars**, which we are not porting. `picture-bg` uses the cover as a full-bleed lyric backdrop. Android v1 still delivers “cover-art colour background” via Coil + Palette on the now-playing chrome, plus `picture-bg` using the cover as a blurred backdrop.

### Lyric card

Select lines → configure colours/overlay → export image and share. Port the web `LyricCard` flow to Compose + `ShareCompat`.

### Deep links

Web: hash route `#/bai-hat/{path}`. Android: `https://…/bai-hat/{path}` and/or `lilimusic://bai-hat/{path}` opening the player for that song (still must be playable / have `file_name`).

### Offline

Not in the web app. v1 requirement: download audio + lyric + cover with Media3 `DownloadManager`, play offline, keep metadata in Room.

## 4. Architecture

Single-activity Jetpack Compose app, unidirectional data flow, Hilt, now-in-android-style modules.

```
:app                          Application, Hilt, NavHost, deep links, Media3 service entry
:core:common                  Dispatchers, result types, extensions
:core:model                   Song, Lyric, PlayMode, Settings (pure Kotlin)
:core:network                 Retrofit + OkHttp + kotlinx.serialization, base URL
:core:database                Room (library cache, downloads, listen state)
:core:datastore               Preferences (sort, play mode, lyric/app settings)
:core:data                    Repositories (single source of truth)
:core:playback                Media3 ExoPlayer + MediaSessionService + DownloadManager
:core:lyrics                  LRC / XLRC / TRC parser + karaoke timing (no UI)
:core:ui                      Theme, shared composables, Palette helpers
:feature:library              Song list, sort, search
:feature:player               Now-playing chrome, controls, cover colour
:feature:lyrics               Synced lyric UI
:feature:settings             Settings screen
:feature:downloads            Download list / actions
```

Dependency rule: `feature:*` → `core:*` → never the reverse. `core:playback` does not depend on Compose. `core:lyrics` is pure Kotlin so parser tests stay JVM.

Package: `com.anhtuta.lilimusic`.

minSdk 26, compileSdk 37, targetSdk 35, JVM 17.

### Stack (pinned in `gradle/libs.versions.toml`)

Kotlin, Jetpack Compose + Material 3, Navigation Compose, Hilt, Media3 (ExoPlayer + MediaSessionService + DownloadManager), Retrofit + OkHttp + kotlinx.serialization, Room, DataStore, Coil, Palette, WorkManager.

### Playback service

Foreground `MediaSessionService`. App UI talks through a `PlayerConnector` / `PlaybackRepository` (MediaController). Notification and lock screen come from the session. Playback speed is a session command. Listen counting is a WorkManager one-shot scheduled 60s after a play-from-start, cancelled if the song changes or playback stops before 60s.

### Data flow

```
API ──► RemoteDataSource ──► SongRepository ──► feature ViewModels ──► Compose
                 │                    ▲
                 └──► Room cache ─────┘
Downloads: Media3 DownloadManager + Room DownloadEntity
Settings: DataStore ──► SettingsRepository
```

## 5. Testing strategy

Every production unit of code has tests. Prefer JVM unit tests; use Robolectric / Compose UI tests where the code is Android-specific.

| Layer | How |
| --- | --- |
| Lyric parser, sort/search, shuffle-current-first, URL joining, listen timer policy | JVM unit tests with fixture `.lrc` / `.xlrc` / `.trc` files |
| Network DTOs / Retrofit | MockWebServer |
| Room DAOs | Robolectric or in-memory Room |
| DataStore mappings | JVM + Robolectric |
| ViewModels | Turbine / coroutines-test |
| Compose screens | `compose-ui-test-junit4` (meaningful interactions, not screenshot-only) |
| Playback | Fake `Player` / Media3 test utils; no emulator required for unit CI |
| Gradle / CI | `./gradlew check` = unit tests + Android Lint + ktlint + detekt |

CI (GitHub Actions): build, unit tests, lint, ktlint, detekt, Conventional Commit lint on PRs. Instrumented tests are optional later (Firebase Test Lab / emulator job) and are **not** a v1 CI gate if they need devices.

## 6. Phases and tasks

Each task is a later `docs/tasks/NNN-*.md` plus small commits. Task numbers are stable; later PRs implement them in order unless noted.

### Phase 0 — Project initialization *(this run)*

| ID | Task |
| --- | --- |
| 001 | Gradle wrapper, version catalog, app + empty module stubs, Hilt, API base URL `BuildConfig`, `.gitignore`, README, GitHub Actions (build + unit tests + lint), trivial passing test |

### Phase 0b — Agent / repo tooling *(this run)*

| ID | Task |
| --- | --- |
| 002 | `AGENTS.md` + tool pointers, Cursor rules, task template, GitHub PR/issue templates, `.editorconfig`, ktlint + detekt, CI gates, Dependabot |

### Phase 1 — Domain + network + cache *(task 003)*

| ID | Task | Status |
| --- | --- | --- |
| 010 | `Song` / `LyricRef` models; playable filter (`file_name`); image URL absolutizing | Done in 003 |
| 011 | Retrofit API (`song/all`); MockWebServer tests. Stream URL is built, not fetched. Lyric client deferred. Listens client in **006** | Partial (003 + 006) |
| 012 | Room library cache + DataStore **sort** keys. Full settings keys deferred | Partial (003) |
| 013 | `SongRepository`: fetch, cache, filter Zing, sort, local search | Done in 003 |
| 014 | Hilt modules wiring network/db/datastore | Done in 003 |

### Phase 2 — Library UI *(task 003)*

| ID | Task | Status |
| --- | --- | --- |
| 020 | Navigation shell + Material 3 theme (light/dark); library start destination; placeholder player route | Done in 003 |
| 021 | Library list UI (title, artist, cover). Listens / SOY / type / date as list columns deferred | Partial (003) |
| 022 | Sort controls matching web columns/orders; persist sort | Done in 003 |
| 023 | Local search | Done in 003 |
| 024 | Empty / error / offline-cached states; pull to refresh | Done in 003 |

### Phase 3 — Playback *(task 006)*

| ID | Task | Status |
| --- | --- | --- |
| 030 | Media3 `MediaSessionService` + notification/lock screen | Done in 006 |
| 031 | Play from library; audio URL; placeholder art | Done in 006 |
| 032 | Sequence / shuffle (current first) / repeat-one | Done in 006 |
| 033 | Playback speed | Done in 006 |
| 034 | Listen count after 60s (WorkManager, same-path guard) | Done in 006 |
| 035 | Mini-player + now-playing controls | Done in 006 |

Also in 006 (pulled forward from later phases because playback needs them): persist play mode / last song / speed; in-app `bai-hat/{path}` route; `POST_NOTIFICATIONS` + `FOREGROUND_SERVICE_MEDIA_PLAYBACK`. HTTPS App Links and the rest of Play Store packaging stay 063–064.

### Phase 4 — Lyrics engine *(task 008)*

| ID | Task | Status |
| --- | --- | --- |
| 040 | Time decoder + meta tags (`offset`, `ti`, `ar`, `by`, `date`, `source`) | Done in 008 |
| 041 | `.lrc` parser | Done in 008 |
| 042 | `.xlrc` `[tranN]` parser | Done in 008 |
| 043 | `.trc` word-level parser | Done in 008 |
| 044 | Karaoke fill + empty-word countdown dots | Done in 008 |
| 045 | User ±100ms offset combined with file offset | Done in 008 (session-only, matches web) |
| 046 | Fetch lyric (treat `Lyric doesn't exist!` as missing) | Done in 008 |

Also in 008 (pulled forward from Phase 5 because lyrics need to be visible): synced lyric list on the player (highlight, autoscroll, karaoke, countdown, `.xlrc` translations). Alignment / gigantic line / word-appear / line-appear / blur / lyric themes stay **050–051**. Room caches lyric *bodies* for offline replay; Media3 file downloads stay **060**. Tap-a-line-to-seek is an Android extra (web does not seek from lyric taps).

### Phase 5 — Player visuals *(task 009)*

| ID | Task | Status |
| --- | --- | --- |
| 050 | Synced lyric list UI (alignment, gigantic line, word/line appear, translation, blur). Highlight/autoscroll/karaoke/countdown done in **008**; remaining visual toggles here | Done in 009 |
| 051 | Lyric themes (dark / light / picture-bg) | Done in 009 |
| 052 | Cover-art Palette background | Done in 009 |
| 053 | Settings screen persistence | Done in 009 |

`.xlrc` translations are gated by the `translate` setting (default **off**, matching web). Speed ±0.1× stays on the player and is also on Settings (already persisted in **006**). In-app volume, waveform, lyric-card export, and sleep timer stay later / out of v1. Downloads are **060–061**.

### Phase 6 — Offline, share, deep links, store

| ID | Task | Status |
| --- | --- | --- |
| 060 | Media3 downloads (audio + lyric + cover) + download UI | Done in 010 |
| 061 | Offline playback using downloaded files | Done in 010 |
| 062 | Lyric card compose / export / share | |
| 063 | App links / deep links `bai-hat/{path}` | |
| 064 | Play Store packaging: applicationId, icons, POST_NOTIFICATIONS, FOREGROUND_SERVICE_MEDIA_PLAYBACK, privacy policy, no `RECORD_AUDIO` | |
| 065 | Release smoke: Playable-only library, notification controls, offline, lyric formats | |

## 7. Workflow (mandatory)

1. Write/update the overall plan in `docs/` when scope changes.
2. For every task, write `docs/tasks/NNN-task-name.md` from `docs/tasks/TEMPLATE.md` and **commit it before implementing**.
3. Every piece of code has tests.
4. Commits are small, one purpose, Conventional Commits (`feat`, `fix`, `docs`, `test`, `chore`, `refactor`, `ci`).
5. `./gradlew check` must pass before a PR is considered done.

## 8. Risks

| Risk | Mitigation |
| --- | --- |
| Private web/backend source not cloned here | Behaviour locked to live API + recovered web source maps; re-verify if source access appears |
| `sortBy=createdDate` vs `created_date` | Golden test against live API during task 013 |
| Missing lyric returns HTTP 200 HTML | Parser/repository treats exact body `Lyric doesn't exist!` as empty |
| `file` query params contain Unicode / spaces | Always URL-encode; MockWebServer + one live smoke later |
| Media3 notification / Play policy | Playback FGS is `mediaPlayback`; download FGS is `dataSync`; never `RECORD_AUDIO` |
| Unpaged `/api/song/all` growing | Fine for ~150 songs; add local paging in UI if the list grows |
| Relative vs absolute `image_url` | Single `ImageUrlResolver` with tests |
| Shuffle “current first” easy to get wrong | Dedicated unit tests around playlist mutation |
| Lyric timing vs ExoPlayer position | Use player `currentPosition` + both offsets; test with fake clock |
| Download storage / scoped storage | Media3 download directory in app-private storage (no extra storage permission on 26+) |

## 9. This-run deliverable

Task **010** (`docs/tasks/010-downloads-offline.md`): Media3 downloads of audio + lyric + cover, Downloads screen, offline playback from the download cache, WorkManager network constraint on listen PUT. Lyric-card export (062), HTTPS App Links (063), Play Store packaging (064), and release smoke (065) remain later.
