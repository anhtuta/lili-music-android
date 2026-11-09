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
| `PUT` | `/api/song/listens?path=` | Count a listen. **Web:** 60s after play from position 0 on the same `path`. **Android (v1):** after **≥ 50%** of track duration has been played on the same `path` (any start position; pause does not reset progress). Legacy `?file=` still works; Android uses `path`. |
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

Local search is client-side substring match on title/artist (web filters the Song column). Search only hides rows; the play queue is always the full playable catalog in API sort order. The extra Android asc/desc toggle re-fetches with `sortOrder`. Do not re-sort the unfiltered list client-side.

The **web** prefers `zing_id` over `file_name` when both exist. **Android v1 does the opposite of that streaming path:** hide any song without a non-empty `file_name`, including Zing-only rows.

### Audio URL

`{BASE}/api/song/file?file={file_name}` (URL-encode `file`). Do **not** use `/api/zing/mp3/streaming`.

## 3. Behaviour to match (web)

Sources: `pages/BaiHat/BaiHat.js`, `Player.js`, `Setting.js`, `BaiHatService.js`, `UtilService.js`, `constants/Constants.js`, lyric-card components.

### Playback modes (`playType`)

`SEQUENCE` | `SHUFFLE` | `REPEAT_ONE` (mutually exclusive; shuffle and repeat-one each toggle back to sequence).

Shuffle: Fisher–Yates the playlist, then **move the current song to index 0**. Leaving shuffle restores the original list and relocates the current song.

End of list in sequence/shuffle: stop and warn (web does not wrap). Repeat-one: next/prev replay the same song. Previous **always** goes to the previous track (no 3s restart-current rule). Previous on the first track is a no-op (keep position) and shows “This is the first song of playing list!”. Unknown duration (`C.TIME_UNSET`) falls back to 600s until a real length arrives.

Speed: ±0.1×, persisted, clamped 0.5–2.0× on Android. Volume persisted on web; Android uses system/media volume plus optional in-app volume if we keep it — default to Media3 session volume.

MediaSession: title, artist, artwork, play/pause/stop, next/prev, seek back/forward 5s. Map this to Media3 `MediaSessionService` (notification + lock screen).

### Lyrics

Formats:

- `.lrc` — line timestamps `[mm:ss.xx]` (also `[hh:mm:ss.xx]`). Duplicate timestamps on one line duplicate the line at each time.
- `.xlrc` — `.lrc` plus `[tranN]…` translation lines bound to the preceding timed line.
- `.trc` — line timestamp plus word durations `<ms>word`. Empty words (`<n>` with blank text) are countdown slots.

Meta tags: `offset`, `by` / deprecated `author`, `date` / deprecated `createdDate`, `source`, `ti`, `ar`. File `offset` is milliseconds added to all times. User offset is ±100ms per tap, client-only, **not persisted** on web (session only); Android should match unless we later persist it.

Countdown dots: on empty words **only when a following word exists**, if gap ≥5s show dots in the last 5s, or if gap ≥3s show dots in the last 3s (`•`…`•••••` from `setBulletText`).

Karaoke: fill the current word left-to-right as `(now - start) / (end - start)`. Web default is **on** (`karaoke-mode` on the lyric container). Light fill `#2199F9`, dark fill `#FC0`.

Tick interval is **30ms**, matching web `setPlayLyricInterval`.

Missing lyric body equals `Lyric doesn't exist!` or empty `lyric` field → show the “no lyric yet” contribution message with tappable Mail (`mailto:taanhtu95@gmail.com`) and FB (`https://fb.com/anhtuta95`) links from web `NO_LYRIC`.

### Settings (persisted in web `localStorage` key `LILIANA_SETTINGS`)

Lyric playground: `lyricAlign` (left/center/right), lyric theme `dark` | `light` | `picture-bg`, `karaokeMode`, `blurMode`, `giganticLine`, `wordAppear`, `lineAppear`, `translate`, `speed`.

Web defaults when a key is missing: `playType` = `SEQUENCE`, `speed` = `1.0`, `karaokeMode` = **true**, lyric theme = **`picture-bg`**, `appTheme` = **light**, `giganticLine` = **on** (HTML class `div-res-big-active-line` on first run), `blurMode` / `wordAppear` / `lineAppear` / `translate` = off. User lyric offset is session-only.

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

Foreground `MediaSessionService`. App UI talks through a `PlayerConnector` / `PlaybackRepository` (MediaController). Notification and lock screen come from the session. Playback speed is a session command. Listen counting (Android-only vs web) accumulates played media time on the current `path`; when **≥ 50%** of duration is reached, WorkManager runs `PUT /api/song/listens?path=`. Pause cancels the pending delay but keeps progress; **path change** or queue restart resets the session. Seek back/forward increments are 5 seconds.

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
| 021 | Library list UI (title, artist, cover). Listens / SOY / type / date + cover preview | Done in 003 + **010** |
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
| 034 | Listen count after 60s (WorkManager, same-path guard; cancel only on path change) | Done in 006, aligned in **010** |
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

`.xlrc` translations are gated by the `translate` setting (default **off**, matching web). Speed ±0.1× stays on the player and is also on Settings (already persisted in **006**). Gigantic line defaults **on**. In-app volume, waveform, lyric-card export, and sleep timer stay later / out of v1. Downloads are **060–061**.

### Phase 5b — Web parity *(task 010)*

Owner-approved mismatches vs class `BaiHat.js` / `Player.js` after PR #18. See [docs/tasks/010-web-parity-fixes.md](tasks/010-web-parity-fixes.md) and §10 below.

### Phase 6 — Offline, share, deep links, store

| ID | Task | Status |
| --- | --- | --- |
| 060 | Media3 downloads (audio + lyric + cover) + download UI | Done in 011 |
| 061 | Offline playback using downloaded files | Done in 011 |
| 062 | Lyric card compose / export / share | Done in this PR |
| 063 | **External** App Links / intent-filters for `bai-hat/{path}` (Play Store / HTTPS). In-app navigation to `bai-hat/{path}` already exists from **006**. | Done in this PR |
| 064 | Play Store packaging: applicationId, icons, POST_NOTIFICATIONS, FOREGROUND_SERVICE_MEDIA_PLAYBACK, privacy policy, no `RECORD_AUDIO`, R8, optional upload-key signing | Done in this PR |
| 065 | Release smoke: minified APK + aapt gate; manual arm64 emulator checklist | Done in this PR |
| 066 | Wrap long karaoke lyric lines inside the pane (no one-glyph overflow column) | Done in 011 / PR #21 |
| 067 | Full-height lyrics once Now playing is scrolled to the end; Previous label unclipped | Done in 011 / PR #21 |

### Phase 7 — Compact now-playing, transport icons, lyric-card fonts, download cancel

Owner-picked web-matching follow-ups after PR #21. Task plans: [070](tasks/070-compact-lyrics.md), [071](tasks/071-transport-icons.md), [072](tasks/072-lyric-card-fonts.md), [073](tasks/073-cancel-download.md), [074](tasks/074-download-action-icon.md), [075](tasks/075-smooth-trc-karaoke.md), [076](tasks/076-icon-buttons-tooltips.md).

| ID | Task | Status |
| --- | --- | --- |
| 070 | Hide the cover on compact width (&lt; 720dp, matching web `#div_left_wrapper { display: none }`) so lyrics fill the body immediately; pin transport / seek / speed at the bottom. Wider screens keep cover + lyrics side by side. Remove the 067 scroll-to-full-height column where it no longer applies | Done in PR #22 |
| 071 | Replace Previous / −5s / Play / +5s / Next text with icon buttons (web `ll-btn-prev` / `ll-btn-next` / play-pause) plus contentDescriptions | Done in PR #22 |
| 072 | Bundle web lyric-card Google Fonts (Lobster, Pacifico, Lavishly Yours, Borel; OFL) as `res/font` and use them in the font picker / JPEG export | Done in PR #22 |
| 073 | Cancel an in-progress download: stop Media3, remove partial audio/lyric/cover + Room row, update Downloads / library / player UI | Done in PR #22 |
| 074 | Replace the text Download action with a state icon (`Download` / progress / `DownloadDone`) plus contentDescriptions on library rows and Now playing | Done in PR #22 |
| 075 | Smooth `.trc` karaoke fill (interpolate playback position between ExoPlayer samples; draw-phase fill). `.xtrc` stays out of v1 (web: “future”) | Done in PR #23 |
| 076 | Replace remaining text action buttons with Material Icons + long-press `PlainTooltip` via a shared `LiliIconButton`. Keep non-button text (labels, `1.0×`, sort chips, switches) | Done in PR #23 |

### Phase 8 — Now Playing polish (transport & pane)

Owner review of compact Now Playing screenshots (dark + light phone). Tasks **080–089** are the first slice of [080–098](tasks/080-098-now-playing-polish.md). Web player remains `BaiHat.js` / `Player.js`.

| ID | Task | Status |
| --- | --- | --- |
| 080 | One cycling play-mode icon after Next: Sequence (`Repeat`) → Shuffle (`Shuffle`) → Repeat one (`RepeatOne`). Tooltip `Play mode: {current}`. Keep shuffle-current-first | Done in PR #24 |
| 081 | Keep `=====` lyric lines as plain lyric text (no chrome change) | Done in PR #24 |
| 082 | Fade top and bottom edges of the lyric pane (gradient mask) | Done in PR #24 |
| 083 | Drag handle between lyrics and controls: drag down expands lyrics fullscreen; drag up / Back / tap restores | Done in PR #24 |
| 084 | Single control row: speed chip, previous, −5s, play, +5s, next, play mode. Speed ±0.1 in 0.5–2.0 | Done in PR #24 |
| 085 | Large filled circular Play/Pause (~64.dp, primary container) | Done in PR #24 |
| 086 | Lyric offset hidden until pane tap; auto-hide; minus/plus icons only; session/per-song, never persisted | Done in PR #24 |
| 087 | Remove the in-pane lyric-card button; keep the app-bar one | Done in PR #24 |
| 088 | Hide album text everywhere on Now Playing | Done in PR #24 |
| 089 | Thinner seek bar (~4.dp track, small thumb, played vs remaining contrast) | Done in PR #24 |

### Phase 9 — Now Playing polish (chrome & app bar)

Tasks **090–098** complete [080–098](tasks/080-098-now-playing-polish.md) (gradient, contrast, edge-to-edge, app-bar song info, rounded pane).

| ID | Task | Status |
| --- | --- | --- |
| 090 | Subtle cover Palette **gradient** on Now Playing; theme-based gradient fallback; never flat grey | Done in PR #24 |
| 091 | Transport icons full `onSurface`; active state uses the accent | Done in PR #24 |
| 092 | Light effective theme uses `#2199F9` + dark-grey unsung + `#DFDFDF` pane; dark keeps `#FC0`. Sung words use full karaoke fill (dimming removed at owner request) | Done in PR #24 |
| 093 | App-bar back / download / lyric card / settings: contentDescription + long-press tooltip (`LiliIconButton`) | Done in PR #24 |
| 094 | Swipe down on Now Playing closes to the previous screen / mini player (no conflict with 083 or lyric scroll) | Done in PR #24 |
| 095 | Audit library, now playing, downloads, settings, lyric card for dark-on-dark icons | Done in PR #24 |
| 096 | `enableEdgeToEdge` + `SystemBarStyle` from **in-app** theme; light status-bar icons dark on light | Done in PR #24 |
| 097 | App bar shows cover thumbnail + title/artist; drop “Now playing” title and the duplicate block below lyrics | Done in PR #24 |
| 098 | ~16.dp corner radius on the lyric pane; clip content | Done in PR #24 |

### Phase 10 — Now Playing lyrics & controls *(this PR)*

Wrap/center/marquee lyric UI and symmetric transport row. Task plans: [099](tasks/099-wrapped-karaoke-fill.md)–[105](tasks/105-deemphasize-artist.md).

| ID | Task | Status |
| --- | --- | --- |
| 099 | Karaoke fill progresses across every wrapped visual line (not only the last). See [099](tasks/099-wrapped-karaoke-fill.md) | This PR |
| 100 | Wrapped lyric lines use ~1.4× lineHeight (`LineHeightStyle` center, trim none). See [100](tasks/100-lyric-wrap-line-height.md) | This PR |
| 101 | Auto-scroll centers the active lyric line in the pane (web `scrollLyric`). See [101](tasks/101-center-active-lyric.md) | This PR |
| 102 | Transport row: equal-weight left/right groups, centered Play, matching speed/play-mode targets. See [102](tasks/102-control-row-symmetry.md) | This PR |
| 103 | Compact Now Playing download / lyric-card / settings glyphs (~20.dp) with ≥40.dp touch targets. See [103](tasks/103-compact-app-bar-actions.md) | This PR |
| 104 | Overflowing app-bar title and artist marquee slowly; drag to scroll; fitting text stays still. See [104](tasks/104-app-bar-marquee.md) | This PR |
| 105 | App-bar artist is muted (`onSurface` ~0.65 alpha) so the title stands out. See [105](tasks/105-deemphasize-artist.md) | This PR |

### Phase 11 — Library compact UI & test CI *(this PR)*

Library search/sort/rows/overflow menu plus launcher unit-test hardening. Task plans: [106](tasks/106-compact-library-search.md)–[111](tasks/111-launcher-activity-test-ci.md).

| ID | Task | Status |
| --- | --- | --- |
| 106 | Compact Library search (~44–48.dp) with placeholder. See [106](tasks/106-compact-library-search.md) | This PR |
| 107 | One sort pill + dropdown instead of chips. See [107](tasks/107-library-sort-pill.md) | This PR |
| 108 | Library row: title, artist, listens, soy only (no type/date wrap). See [108](tasks/108-library-row-fields.md) | This PR |
| 109 | Listens (icon) left, soy badge right on one row. See [109](tasks/109-listens-soy-row.md) | This PR |
| 110 | Row overflow menu: song info dialog + 074 download states. See [110](tasks/110-song-more-menu.md) | This PR |
| 111 | `LauncherActivityTest` launches `MainActivity` by class (`FLAG_ACTIVITY_NEW_TASK`); unit tests print full exception traces. See [111](tasks/111-launcher-activity-test-ci.md) | This PR |

### Phase 12 — Now Playing refinement *(this run)*

Owner follow-up screenshots: leaner app bar, edge-to-edge lyrics, control insets, lyric text size presets, listen-count fix. Task plans: [112](tasks/112-now-playing-overflow-menu.md)–[117](tasks/117-fix-listen-count-schedule.md) (no 116).

| ID | Task | Status |
| --- | --- | --- |
| 112 | Download + Lyric card in gear overflow menu; smaller settings icon. See [112](tasks/112-now-playing-overflow-menu.md) | This run |
| 113 | Full-width lyric area; drop outer rounded box; keep text padding aligned with chrome. See [113](tasks/113-full-width-lyric-pane.md) | This run |
| 114 | Bottom inset / navigation padding on transport + seek. See [114](tasks/114-player-controls-bottom-inset.md) | This run |
| 115 | Five lyric font sizes (default = level 2 Normal). See [115](tasks/115-lyric-font-size-levels.md) | This run |
| 117 | Schedule listen count when playback starts even if first ExoPlayer position is slightly &gt; 0. See [117](tasks/117-fix-listen-count-schedule.md) | This run |

### Phase 13 — Library chrome & overflow trigger *(done on `tuta`)*

Owner screenshots: Now Playing ⋮ trigger; library sort left, scrollable chrome, scrollbar, row ⋮ alignment. Task plans: [118](tasks/118-now-playing-overflow-morevert.md)–[122](tasks/122-library-row-more-align.md). Learning: [17 — Library chrome](learning/17-phase13-library-chrome.md).

| ID | Task | Status |
| --- | --- | --- |
| 118 | Now Playing overflow trigger uses ⋮ (`MoreVert`), not gear. See [118](tasks/118-now-playing-overflow-morevert.md) | Done |
| 119 | Library sort pill start-aligned. See [119](tasks/119-library-sort-pill-start.md) | Done |
| 120 | Library title, search, sort scroll with the list (not pinned). See [120](tasks/120-library-scrollable-chrome.md) | Done |
| 121 | Vertical scrollbar on library list. See [121](tasks/121-library-scrollbar.md) | Done |
| 122 | Row ⋮ aligns with search/settings right edge. See [122](tasks/122-library-row-more-align.md) | Done |

### Phase 14 — Library playback chrome *(done on `tuta`)*

Now Playing: no pull-dismiss. Library: search inset, thicker scrollbar, playing/pause cover badges, auto-scroll to current track. Task plans: [123](tasks/123-remove-now-playing-pull-dismiss.md)–[127](tasks/127-library-scroll-to-now-playing.md). Learning: [18 — Library playback UX](learning/18-phase14-library-playback-ux.md).

| ID | Task | Status |
| --- | --- | --- |
| 123 | Remove Now Playing pull-down back gesture. See [123](tasks/123-remove-now-playing-pull-dismiss.md) | Done |
| 124 | Search right edge clears scrollbar; align with settings. See [124](tasks/124-library-search-scrollbar-inset.md) | Done |
| 125 | Thicker library scrollbar thumb. See [125](tasks/125-library-scrollbar-thumb-width.md) | Done |
| 126 | Playing/pause GIF overlay on current song cover. See [126](tasks/126-library-now-playing-cover-badge.md) | Done |
| 127 | Scroll library to now-playing song on show. See [127](tasks/127-library-scroll-to-now-playing.md) | Done |

### Phase 15 — Library scrollbar refinement

Refine the library scrollbar thumb shape, width, visibility, touch target, geometry, and drag lifecycle. Task plans: [128](tasks/128-library-scrollbar-half-pill.md)–[131](tasks/131-keep-scrollbar-visible-while-dragging.md).

| ID | Task | Status |
| --- | --- | --- |
| 128 | Wider half-pill scrollbar thumb; preserve row overflow actions. See [128](tasks/128-library-scrollbar-half-pill.md) | Done |
| 129 | Compact scrollbar that auto-hides when the library is idle. See [129](tasks/129-auto-hide-scrollbar.md) | Done |
| 130 | Responsive rail touch target, end jump, and stable thumb geometry. See [130](tasks/130-scrollbar-touch-and-stability.md) | Done |
| 131 | Keep scrollbar visible for the complete drag gesture. See [131](tasks/131-keep-scrollbar-visible-while-dragging.md) | Done |

### Phase 16 — Lyric offset UX

| ID | Task | Status |
| --- | --- | --- |
| 132 | Reveal offset controls on pane touch or scroll (not only line seek). See [132](tasks/132-lyrics-offset-reveal-scroll.md) | Done |
| 133 | Move offset controls to bottom of lyric pane. See [133](tasks/133-lyrics-offset-bottom.md) | Done |
| 134 | Show current offset ms after nudge. See [134](tasks/134-lyrics-offset-feedback.md) | Done |
| 137 | Offset overlay shows meta + user ms (file tag included). See [137](tasks/137-lyrics-offset-combined-label.md) | In progress |

### Phase 17 — Lyric expand chrome

| ID | Task | Status |
| --- | --- | --- |
| 135 | Animate transport chrome on lyric handle tap/drag. See [135](tasks/135-lyric-chrome-expand-animation.md) | Done |

### Phase 18 — Android listen threshold

| ID | Task | Status |
| --- | --- | --- |
| 136 | Count a listen after ≥ 50% of track duration played (any start position). See [136](tasks/136-listen-count-half-duration.md) | Done |

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
| Debug APK opens a blank `ComponentActivity` | Never put `MAIN`/`LAUNCHER` on Compose `ui-test-manifest`’s `ComponentActivity`; keep that filter only on `MainActivity`. See task [013](tasks/013-debug-launcher.md) |

## 9. This-run deliverable

PR #24 merged Phases **8–9** (**080–098**) on main. Phases **10–11** (**099–111**) cover wrap/center/app-bar polish, Library compact UI, and launcher test CI. Phase **12** (**112–117**) is the current run: overflow app bar, full-width lyrics, control insets, lyric font levels, listen-count fix. See [112](tasks/112-now-playing-overflow-menu.md)–[117](tasks/117-fix-listen-count-schedule.md).

Owner still must host Digital Asset Links, host the privacy policy URL, and create the Play upload keystore. The Lumen backend is unchanged.

## 10. Intentional differences from the web app

These Android extras / choices are **kept as-is** (not bugs):

| Item | Android behaviour | Web |
| --- | --- | --- |
| **4** | Speed clamped to **0.5–2.0×** in 0.1 steps | Unbounded ±0.1 in the settings panel |
| **7 / 21** | Restore last song **paused** on cold start (`lastSongPath` in DataStore) | No last-song restore |
| **11** | Extra asc/desc toggle next to sort chips | Column click re-fetches with a **fixed** order per column |
| **12** | Instant search (no debounce) | Search box filters as you type (DOM filter); no debounce either, but Android is Compose state |
| **13** | Tap a lyric line to seek | Lyric taps do not seek |
| **20** | Cover Palette tints now-playing chrome | `getAverageRGB` only tinted waveform bars (not ported) |
| **22** | Custom scheme `lilimusic://bai-hat/{path}` always; HTTPS path-style App Links after the owner hosts `assetlinks.json`. Hash URLs rewrite only when the fragment is in the Intent. | Hash route `#/bai-hat/{path}` |
| **Downloads** | Media3 downloads + offline playback from cache (060/061). Per-song and Downloads-screen actions are icons with long-press tooltips (074/076), not “Download” / Cancel / Delete text | No download / offline library |
| **Lyric card** | Tap-to-toggle whole lines; share sheet JPEG instead of a file download. Decorative Google Fonts are bundled (072); Open Sans / Verdana / Georgia still use the system sans/serif | Desktop `getSelection()` + Google Fonts (including Open Sans from `index.html`) + `html-to-image` download |
| **App Links hosts** | Path-style `https://blog.nenmongvietnam.com/bai-hat/{path}` and `https://anhtuta.github.io/lili-player/bai-hat/{path}` (Android-first until the web host rewrites those paths) | Only `#/bai-hat/{path}` on the HashRouter origin |
| **Transport icons** | Material Icons (`SkipPrevious`, `Replay5`, `PlayArrow`/`Pause`, `Forward5`, `SkipNext`) via `LiliIconButton` (contentDescription + long-press tooltip) | CSS triangle arrows (`ll-btn-prev` / `ll-btn-next` / `.ll-play-pause .arrow`) |
| **Icon-only chrome** | App bar, speed ±, lyric offset ±, lyric-card / downloads / mini-player actions are icons with tooltips. Sort **field** chips, settings chips, and switches stay labeled | Web mix of text (`Back`, `Settings`) and CSS arrows; no Material tooltips |
| **Now-playing chrome** | Separate Now playing screen; app bar is cover + title/artist (097); compact controls are one Compose row (084) | Cover/lyrics live in `#lyric_playground`; `.ll_player` under it has title/artist/album plus shuffle \| prev \| play \| next \| repeat |
| **Play mode control** | One cycling icon after Next (080): Sequence → Shuffle → Repeat one. `PlayMode.cycle()`. Shuffle still current-first | Two toggles in `Player.js` (`.ll-shuffle` / `.ll-repeat`); unused `playTypeList` in `Constants.js` is the cycle Android uses |
| **Album on Now Playing** | Hidden (088), even when `song.album` is set | `.song_album` shows `({album})` or `(No Album)` |
| **Lyric offset UI** | Hidden until the pane is tapped; auto-hides; minus/plus icons only (086). Line tap still seeks (Android extra) | Settings overlay caret-up/down (`−100` / `+100` ms, client only) |
| **Lyric edge fade / handle / swipe-down** | Fade mask (082), drag handle to fullscreen lyrics (083), swipe down to close the route (094) | Overflow clip only; `fa-arrows-alt` fullscreen; single-page (no now-playing route) |
| **`.xtrc`** | `LyricFormat.fromFileName` maps `.xtrc` to the `.trc` parser (no `[tranN]` translations). Not a translation format in v1 | Comment in `BaiHat.js`: “Will support `.xtrc` in the future”. Unknown extensions also take the TRC DOM path with `hasTranslation = false` |

The extra sort-order toggle (**11**) still talks to the API (`sortOrder`); it does not locally re-sort an unfiltered catalog. Online play uses the **full playable catalog** in API order (`catalogIndex`); search only filters the view. Offline play uses that same catalog filtered to completed downloads.
