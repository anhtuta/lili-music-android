# 003 — Data layer and library screen

## Goal

A listener can open the app, see the playable song catalog (title, artist, cover), search and sort it, pull to refresh, and tap a song to a placeholder player route. The catalog is cached offline-first in Room from `GET /api/song/all`. Zing-only rows (`file_name` null/empty) never appear.

## Approach

Implements PLAN Phase 1 (domain + network + cache, tasks 010–014) plus the library UI slice of Phase 2 (020–024), as one vertical slice. Backend `https://blog.nenmongvietnam.com/laravel/public` is unchanged. v1 is listener-only.

- **`:core:model`** — `Song` domain model, `LyricRef` (lyric file name only), `SongSort` / `SortOrder` whitelist matching the web `BaiHatTable.js` columns, playable filter, local title/artist search, local comparators, `ImageUrlResolver` (prefix relative `image_url` with the API base URL; leave absolute URLs; null → no cover), stream URL `{BASE}/api/song/file?file=` with query encoding.
- **`:core:network`** — Retrofit + OkHttp + kotlinx.serialization. `GET /api/song/all?sortBy=&sortOrder=` returns an unpaged JSON array. Public API accepts only `SongSort` / `SortOrder` so unknown `sortBy` cannot be sent (live API returns HTTP 500). Do **not** add lyric / listens / Zing endpoints in this task. Hilt `NetworkModule`. Base URL injected from `:app` `BuildConfig.API_BASE_URL`.
- **`:core:database`** — Room `SongEntity` + DAO as the cache. Store already-resolved image and stream URLs. Replace-all on refresh. Hilt `DatabaseModule`.
- **`:core:datastore`** — persist last sort column/order only (PLAN: persist last sort; full lyric/app settings stay later).
- **`:core:data`** — `SongRepository`, offline-first: Room is the source of truth, `refresh()` writes playable songs from the network. Filter Zing on ingest. Fakes for tests.
- **`:core:ui`** — minimal Material 3 light/dark theme used by the nav host.
- **`:feature:library`** — Hilt `LibraryViewModel` + Compose list: cover (Coil), title, artist, search, sort options (web columns + each column’s default order), loading / error / empty / cached-with-refresh-error, pull to refresh.
- **`:app`** — Navigation Compose; library is the start destination. Tapping a song navigates to a **placeholder** player route (`:feature:player` stub). Hilt provides `ApiBaseUrl`.

`sortBy` whitelist (never send anything else): `title`, `listens`, `song_of_the_year`, `type`, `createdDate`. Default `title` / `ASC`.

### Non-goals

Playback/Media3, lyric fetch/parse/UI, listen counting, downloads, deep links, Zing streaming, admin/login, waveform / `RECORD_AUDIO`, changing hello_lumen.

## Files

- `docs/tasks/003-data-layer-library.md` (this file)
- `docs/PLAN.md` (progress)
- `gradle/libs.versions.toml` (test/runtime aliases as needed)
- `core/common/` — dispatcher qualifier if needed for repository tests
- `core/model/` — `Song`, `LyricRef`, sort, URL helpers, search/sort
- `core/network/` — DTOs, Retrofit API, Hilt module, mapper, MockWebServer fixtures
- `core/database/` — entity, DAO, database, Hilt module
- `core/datastore/` — sort preferences
- `core/data/` — `SongRepository`, offline-first impl, Hilt binds, fakes
- `core/ui/` — Material 3 theme
- `feature/library/` — ViewModel, screen, strings
- `feature/player/` — placeholder route UI
- `app/` — `LiliNavHost`, Hilt `ApiBaseUrl`, wire library as start destination

## Tests

- **model (JVM):** playable filter (null/blank `file_name` out); relative/absolute/null `image_url`; stream URL encodes spaces/Unicode; search substring on title/artist (case-insensitive); sort comparators; unknown `sortBy` cannot be represented.
- **network (MockWebServer):** fixture JSON (playable relative cover, playable absolute cover, playable null cover, Zing-only `file_name: null`); query `sortBy`/`sortOrder`; mapper drops unplayable; HTTP error surfaces.
- **database (in-memory Room + Robolectric):** insert/observe/replaceAll.
- **datastore (Robolectric):** default sort; persist/restore.
- **data (fakes + Turbine):** Room remains source of truth; refresh writes playable only; failed refresh keeps cache; empty remote cache stays empty.
- **library ViewModel (Turbine):** loading, success, empty, search, sort, pull-to-refresh, error-with-cache, error-empty.
- **library Compose (Robolectric + compose-ui-test):** list titles, search filters, empty/error/retry, song click callback.
- **app:** start destination is library; player placeholder shows the selected song id.

## Commit breakdown

1. `docs(tasks): add 003 data-layer and library plan` — this file only.
2. `feat(model): add Song domain, sort whitelist, and URL helpers` (+ tests in the same layer commit or a following `test(model): …`).
3. `feat(network): add Retrofit song catalog client` + MockWebServer tests.
4. `feat(database): add Room song cache` + in-memory DAO tests.
5. `feat(datastore): persist library sort preferences` + tests.
6. `feat(data): add offline-first SongRepository` + fakes/tests.
7. `feat(ui): add Material 3 theme`.
8. `feat(library): add song list screen with search and sort` + ViewModel/Compose tests.
9. `feat(player): add placeholder now-playing route`.
10. `feat(app): host library as the start destination`.
11. `docs: mark Phase 1 library slice progress in PLAN`.

Commits stay Conventional Commits (`feat|test|docs|chore|…`). Implementation commits include tests for that layer.

## Done when

- Task plan committed **before** code (this file).
- Playable songs from `/api/song/all` are cached in Room and shown on the library screen.
- Zing-only songs are hidden. Relative covers resolve against the API base URL. Stream URLs are encoded `file` query params (not fetched yet).
- Search, sort, loading/error/empty, pull to refresh work. Tap opens the placeholder player route.
- No playback, lyrics, downloads, or listen counting.
- `./gradlew check` passes.
