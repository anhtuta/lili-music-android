# 006 — Playback service, player screen, and listen counting

## Goal

A listener can tap a playable library song and hear it stream from `GET /api/song/file?file=`. The full player and a mini-player show cover, title/artist, seek, play/pause, next/prev, and play mode. Sequence / shuffle (current song first) / repeat-one and playback speed match the web player. After 60s of a play-from-start on the same `path`, the app records one listen. Play mode, speed, and last song restore on launch. Lock-screen, notification, Bluetooth, and headset controls work through a Media3 session.

## Approach

Implements PLAN Phase 3 (030–035) as one vertical slice, the same way task 003 combined Phase 1 + library UI. Backend `https://blog.nenmongvietnam.com/laravel/public` is unchanged. Listener only. Package `com.anhtuta.lilimusic`.

PLAN.md Phase 3 also includes **playback speed (033)** even though the launch prompt listed only play-mode. This task includes speed (`±0.1×`, default `1.0`, persisted).

Listen counting follows PLAN, not a wall-clock “60s of accumulated play”: a WorkManager one-shot is scheduled 60s after playback starts from position ~0, cancelled if the song changes or playback stops before 60s, then `PUT /api/song/listens?path=` once per play. Retrofit URL-encodes `path`.

In-app route is `bai-hat/{path}` (web `#/bai-hat/{path}`). HTTPS / verified App Links stay Phase 6 task 063. `POST_NOTIFICATIONS` (API 33+) and `FOREGROUND_SERVICE_MEDIA_PLAYBACK` are added now because the session notification needs them; the rest of Play Store packaging stays 064.

Queue = the current visible library list (search + sort). Tapping a row plays that song in that queue. Shuffle Fisher–Yates then moves the current song to index 0. Leaving shuffle restores the original queue order and relocates the current song. End of list in sequence/shuffle: stop (no wrap). Repeat-one: next/prev / completion replay the same song.

### Modules

- **`:core:model`** — `PlayMode` (`SEQUENCE` | `SHUFFLE` | `REPEAT_ONE`), `PlayQueue` (shuffle-current-first, next/prev, end-of-list), `PlaybackSpeed` (clamp + step).
- **`:core:network`** — `PUT /api/song/listens?path=`. Empty/non-JSON 2xx bodies must not crash. No lyric or Zing endpoints.
- **`:core:database`** — `observeByPath` so the player can resolve a slug. No schema bump.
- **`:core:datastore`** — persist play mode, last song `path`, speed (same preferences DataStore; sort keys unchanged).
- **`:core:data`** — `SongRepository.observeSongByPath`, `recordListen(path)`.
- **`:core:playback`** (UI-free) — Hilt `ExoPlayerFactory`; `MediaSessionService` with audio focus, becoming-noisy, wake mode, Media3 notification; `PlayerController` (MediaController) exposing `StateFlow<PlaybackState>`; `FakePlayerController` for ViewModel tests; listen-count scheduler + `@HiltWorker`.
- **`:feature:player`** — replace the Phase 1 placeholder with now-playing (cover, title/artist, seek + times, play/pause/next/prev, play-mode buttons, speed ±0.1). Mini-player bar composable. Hilt `PlayerViewModel` talks only to `PlayerController` + `SongRepository`.
- **`:feature:library`** — tap plays the visible list through `PlayerController`, then navigates by `path`.
- **`:app`** — `PlayerDestination(path)` / `bai-hat/{path}`; mini-player above the library (hidden on the full player); request `POST_NOTIFICATIONS` on Android 13+ when playback starts; Hilt `WorkerFactory` on `Application`.

Restore last song **prepared and paused** (mini-player visible, no autoplay). Stream URI is the already-encoded `Song.streamUrl`. Placeholder art when `imageUrl` is null.

### Non-goals

Lyrics fetch/parse/UI, downloads, sleep timer, settings screen, cover Palette background, HTTPS App Links, Play Store listing, waveform / `RECORD_AUDIO`, Zing streaming, admin/login, changing hello_lumen.

## Files

- `docs/tasks/006-playback.md` (this file)
- `docs/PLAN.md` (Phase 3 progress)
- `core/model/` — `PlayMode`, `PlayQueue`, `PlaybackSpeed` + tests
- `core/network/` — `LumenApi` / `LumenNetworkDataSource` listen method + MockWebServer tests
- `core/database/` — `SongDao.observeByPath` + DAO test
- `core/datastore/` — `PlaybackPreferences` + tests
- `core/data/` — repository listen + observe-by-path + fakes/tests
- `core/playback/` — service, controller, listen scheduler/worker, Hilt module, tests
- `feature/player/` — screens, ViewModel, strings, tests (placeholder removed)
- `feature/library/` — play-on-click wiring + tests
- `app/` — nav by path, mini-player host, permissions, WorkManager, manifest

## Tests

- **model (JVM):** shuffle current-first with seeded `Random`; leaving shuffle restores order; next/prev; sequence/shuffle stop at end; repeat-one stays on the same index; speed step/clamp; unknown play-mode parse → `SEQUENCE`.
- **network (MockWebServer):** `PUT /api/song/listens?path=` encodes Unicode/spaces; 2xx empty body succeeds; HTTP error surfaces.
- **database (Robolectric):** `observeByPath` emits the matching row.
- **datastore (Robolectric + Turbine):** default `SEQUENCE` / `1.0` / no last path; persist/restore mode, path, speed.
- **data (fakes + Turbine):** `recordListen` forwards encoded path; `observeSongByPath` from cache.
- **playback (JVM fakes):** `ListenCountPolicy` schedules only from start, cancels on pause/stop/song change, one WorkManager enqueue per play-from-start; `FakePlayerController` / `PlayQueue` integration (modes, skip, seek).
- **player ViewModel (Turbine):** play from queue, play/pause, skip, seek, mode toggle, speed, restore last path without autoplay.
- **player Compose (Robolectric):** controls fire callbacks; seek times; mini-player tap; missing cover placeholder.
- **library:** song click starts playback with the visible list as queue.
- **app:** start destination library; `PlayerDestination(path)` shows the player; `POST_NOTIFICATIONS` / FGS media type present in the merged manifest (assert via unit test or lint).

No emulator. Media3 session is covered by the controller abstraction + fakes, not a device.

## Commit breakdown

1. `docs(tasks): add 006 playback plan` — this file only.
2. `feat(model): add PlayMode, shuffle-current-first queue, and speed`
3. `feat(network): add PUT song listens client`
4. `feat(database): observe cached songs by path`
5. `feat(datastore): persist play mode, last song, and speed`
6. `feat(data): record listens and observe songs by path`
7. `feat(playback): add Media3 session service and PlayerController`
8. `feat(playback): count listens 60s after play-from-start`
9. `feat(player): replace placeholder with now-playing and mini-player`
10. `feat(library): play the visible queue from a row tap`
11. `feat(app): host path-based player, mini-player, and notification permission`
12. `docs: mark Phase 3 playback progress in PLAN`

Implementation commits include tests for that layer.

## Done when

- Task plan committed **before** code (this file).
- Library tap streams `file_name` via Media3; Zing-only rows stay hidden; no Zing streaming endpoints.
- Notification + lock screen / headset / becoming-noisy / audio focus work through `MediaSessionService`.
- Sequence / shuffle-current-first / repeat-one and speed match PLAN §3.
- Listen `PUT` fires once per play-from-start after 60s (same-path guard).
- Play mode, speed, and last song restore on launch (paused).
- Full player + mini-player replace the Phase 1 placeholder. Route key is song `path`.
- `POST_NOTIFICATIONS` requested on Android 13+ as needed.
- No lyrics, downloads, sleep timer, or settings screen.
- `./gradlew check` passes.
