# 010 — Downloads and offline playback

## Goal

A listener can download a playable song (audio + lyric body + cover) into app-private storage, watch progress / failure / retry / delete on a Downloads screen, and play that song with lyrics and cover when there is no network. Online behaviour (queue, listen counting, playback modes, lyrics, sorting) stays matched to the web player. Downloads are an Android-only addition; the attached web app has no download feature.

## Approach

Implements PLAN Phase 6 tasks **060** and **061** only. **062–065** stay later. Backend `https://blog.nenmongvietnam.com/laravel/public` is unchanged. Listener only. Package `com.anhtuta.lilimusic`.

The web player (`liliana-player-reactjs` / `BaiHat.js`, `Player.js`) has no download or offline mode. Anything that touches existing web-matched behaviour must keep matching the web. Android extras (downloads, CacheDataSource, offline unavailable marking, WorkManager network constraint on the listen PUT) are documented as extras, not silent web changes.

### 060 — Media3 downloads + Downloads UI

- **Audio:** Media3 `DownloadManager` + `DownloadService` write the stream (`GET /api/song/file?file=`, already on `Song.streamUrl`) into a `SimpleCache` under app-private storage (`filesDir/media-downloads`). No extra storage permission on minSdk 26. Download id = song `path`.
- **Lyric:** on enqueue, `LyricRepository.loadLyric` caches the body in the existing Room `lyrics` table (already used for offline lyric fallback). Missing lyric (`Lyric doesn't exist!`) is not a download failure.
- **Cover:** HTTP GET of the song’s resolved `imageUrl` into `filesDir/covers`. Null `imageUrl` skips cover. Coil / Media3 artwork / `picture-bg` prefer the local file URI when present.
- **Metadata:** new Room `downloads` table (version **3**). Additive `MIGRATION_2_3` only — no destructive migration, songs/lyrics rows must survive. `exportSchema` stays false; the migration test builds the v2 SQL by hand then runs `MIGRATION_2_3`.
- **Entry points:** library row action (Download / progress / Retry / Delete) and a download button on the now-playing screen. Downloads screen in `:feature:downloads`, reachable from the library app bar.
- **Downloads UI:** list with progress, completed, failed + retry, delete one, delete all (confirm). Empty state.
- **Notifications:** reuse existing `POST_NOTIFICATIONS` handling (request on Android 13+ when a download is active, same as playback). `DownloadService` is a foreground service with `foregroundServiceType="dataSync"` (targetSdk 35) and `FOREGROUND_SERVICE_DATA_SYNC`. Channel strings live in `:core:playback`.
- **Hilt:** do not construct `DownloadManager` / `SimpleCache` / ExoPlayer in Compose or ViewModels.

### 061 — Offline playback

- ExoPlayer uses a read-only `CacheDataSource` over the same download `SimpleCache`. A completed download plays from cache whether online or offline; the MediaItem URI stays the catalog stream URL (web-identical URL builder). Non-downloaded songs still stream from the network when online.
- Lyrics come from the Room lyric cache; cover from the local file when downloaded.
- With no network, the library still shows the Room catalog cache (already true). Downloaded rows stay playable; non-downloaded rows are clearly marked unavailable and tapping them does **not** start playback or navigate. When offline, the play queue is the visible list filtered to completed downloads (Android extra; online queue stays the visible library list, matching the web).
- **Listen counting:** still 60s after play-from-start on the same `path`, cancelled on pause/stop/song change (web `BaiHat.js`). The WorkManager request also sets `NetworkType.CONNECTED` so an offline listen PUT is not dropped; it waits until the device is back online (Android extra; web has no offline queue). Failure still `retry()`.

### Modules

- **`:core:model`** — `DownloadStatus`, `SongDownload`, `OfflinePlaybackPolicy` (pure Kotlin).
- **`:core:database`** — `DownloadEntity` / DAO / local data source, DB v3, `MIGRATION_2_3`.
- **`:core:data`** — `NetworkMonitor`, `CoverStore`, `AudioDownloadEngine` (port), `DownloadRepository` coordinating Room + engine + lyric + cover.
- **`:core:playback`** — `SimpleCache` + `DownloadManager` + `LiliDownloadService` (`dataSync`), engine impl, `CacheDataSource` on ExoPlayer, listen-work network constraint.
- **`:feature:downloads`** — screen + Hilt ViewModel (stub filled in).
- **`:feature:library`** — row actions, offline unavailable marking, Downloads nav entry.
- **`:feature:player`** — download button; prefer local cover URI.
- **`:app`** — `DownloadsDestination`, host route, notification permission when downloads are active, merged `dataSync` service.

### Non-goals

Lyric-card export/share (062), HTTPS App Links (063), Play Store packaging beyond the download FGS type (064), release smoke (065), waveform / `RECORD_AUDIO`, Zing streaming, admin/login, changing hello_lumen, sleep timer, cancelling an in-progress download as a first-class control (failed downloads can be retried; completed ones deleted).

## Files

- `docs/tasks/010-downloads-offline.md` (this file)
- `docs/PLAN.md` (Phase 6 060/061 progress; later commit)
- `core/model/` — download types + offline policy + tests
- `core/database/` — downloads table, migration 2→3, DAO tests, migration test
- `core/data/` — repository, cover store, network monitor, fakes, tests
- `core/playback/` — download manager/service, cache data source, listen constraint, tests
- `feature/downloads/` — screen, ViewModel, strings, tests
- `feature/library/` — row actions, offline unavailable, nav entry, tests
- `feature/player/` — download button, local cover, tests
- `app/` — downloads route, permission, manifest assertions

## Tests

- **model (JVM):** `DownloadStatus` mapping; `OfflinePlaybackPolicy` playable iff online or completed; cover display URI prefers local file.
- **database (Robolectric):** DAO upsert/observe/delete/deleteAll; **migration 2→3** creates `downloads` and keeps existing `songs` / `lyrics` rows.
- **data (fakes + Turbine):** enqueue writes QUEUED then follows engine snapshots; lyric load + cover save on download; missing lyric is not a failure; remove deletes metadata + cover file (lyrics cache kept); removeAll; retry re-enqueues failed; `NetworkMonitor` fake emits online/offline.
- **playback (JVM + Robolectric):** listen work request requires `NetworkType.CONNECTED` and still delays 60s; Media3 state mapper; `CacheDataSource` is attached at player factory (assert via a testable factory seam); Fake engine used by repository tests.
- **downloads ViewModel (Turbine):** list/progress/retry/delete/deleteAll; play completed item.
- **downloads Compose (Robolectric):** retry/delete/delete-all callbacks; progress and failed states; empty state.
- **library:** download action; unavailable-offline rows do not play; online tap still plays the visible queue (web parity); Downloads nav callback.
- **player:** download button states; local cover URI used when present.
- **app:** downloads route reachable; `FOREGROUND_SERVICE_TYPE_DATA_SYNC` on the download service; `POST_NOTIFICATIONS` still declared; no `RECORD_AUDIO`.

No emulator. No live backend writes. Media3 `DownloadManager` is covered by a port + fake, not a device.

## Commit breakdown

1. `docs(tasks): add 010 downloads and offline playback plan` — this file only.
2. `feat(model): add download status and offline playback policy`
3. `feat(database): add download metadata and migrate 2 to 3`
4. `feat(data): add DownloadRepository, cover store, and network monitor`
5. `feat(playback): add Media3 downloads and CacheDataSource playback`
6. `feat(playback): require network for deferred listen PUT`
7. `feat(downloads): add downloads screen and ViewModel`
8. `feat(library): add download actions and offline unavailable state`
9. `feat(player): add download button and local cover`
10. `feat(app): host downloads route and dataSync download service`
11. `docs: mark Phase 6 download tasks 060 and 061 in PLAN`
12. `docs(learning): add downloads and offline playback guide` — alone, after code.

Implementation commits include tests for that layer.

## Done when

- Task plan committed **before** code (this file).
- Audio downloads go through Media3 into app-private storage; lyric body and cover are cached for offline.
- Downloads screen shows progress, completed, failed + retry, delete one, delete all.
- Library and player can start / retry / delete a download.
- Downloaded songs play from the download cache online and offline; lyrics and cover work offline.
- Non-downloaded songs are marked unavailable when offline and are not played.
- Listen PUT is scheduled with a network constraint so offline listens send later (Android extra).
- Room v3 migration is additive and tested.
- Download FGS is `dataSync`; `POST_NOTIFICATIONS` is requested for download progress.
- Online queue, play modes, listen 60s-from-start, lyrics, and sort still match the web.
- Learning-guide page appended in its own commit.
- `./gradlew check` passes.
