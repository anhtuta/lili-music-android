# Task 010 — Phase 6: downloads and offline playback

[Learning guide index](README.md) · Task [010](../tasks/010-downloads-offline.md)

**What was built.** A listener can download a playable song into app-private storage (audio via Media3 `DownloadManager`, lyric body in the existing Room `lyrics` table, cover file under `filesDir/covers`). A Downloads screen shows progress, completed, failed + retry, delete one, and delete all. Downloaded songs play from the download `SimpleCache` online and offline. With no network, the library still lists the Room catalog; non-downloaded rows are marked **Unavailable offline** and do not start playback. Listen counting still waits 60s from play-from-start (web match) and now also requires a network connection so an offline listen PUT is sent later instead of being dropped.

**Status:** This PR.

> [!NOTE]
> **Downloads are an Android extra.** The web player (`BaiHat.js` / `Player.js`) has no download or offline mode. Online queue, sort, playback modes, lyrics, and the 60s listen timer still match the web. Offline queue filtering, unavailable marking, `CacheDataSource`, and the WorkManager network constraint are documented extras — not silent web changes.

Missing lyric (`Lyric doesn't exist!`) is not a download failure. Cancelling an in-progress download is not a first-class control (failed → retry, completed → delete). Lyric-card export, App Links HTTPS, and Play Store packaging stay **062–065**.

## New terms

| Term | Meaning | Java / web analogy |
| --- | --- | --- |
| Media3 `DownloadManager` | Fetches the mp3 into a disk cache | A download queue, not `fetch()` into memory |
| `DownloadService` | Foreground service that runs those downloads | A background job with a progress notification |
| `foregroundServiceType="dataSync"` | targetSdk 35 type for file transfer FGS | Distinct from music’s `mediaPlayback` type |
| `SimpleCache` | On-disk Media3 cache under `filesDir/media-downloads` | A private folder, not the public Downloads dir |
| `CacheDataSource` | ExoPlayer reads cache first, network second | Service worker cache, then network |
| `FLAG_IGNORE_CACHE_ON_ERROR` | Corrupt cache → try upstream instead of failing | Fall back to origin if the SW cache is bad |
| `NoOpCacheEvictor` | Never delete cached audio | LRU off; downloads stay until the user deletes |
| Room `downloads` table | Metadata (status, percent, cover path) | A downloads index; audio bytes live in SimpleCache |
| Additive migration | `MIGRATION_2_3` `CREATE TABLE` only | `ALTER`/`CREATE` — never drop the old DB |
| `CoverStore` | HTTP GET of cover into `filesDir/covers` | Save `img.src` to disk for offline `<img>` |
| `NetworkMonitor` | Flow of “do we have INTERNET?” | `navigator.onLine` / a connectivity listener |
| `WorkManager` `NetworkType.CONNECTED` | Do not run the listen PUT until online | A job queue with a network constraint |
| `POST_NOTIFICATIONS` | Android 13+ permission for download progress | Web notification permission, reused from playback |

Kotlin `enum class DownloadStatus` with `fromStorage` is the same pattern as `PlayMode`: unknown strings become `NONE` instead of throwing.

Why two foreground service types? Android 14+ requires a type on every FGS. Playing music is `mediaPlayback`. Copying files is `dataSync`. Using `mediaPlayback` for downloads would be the wrong type and can be rejected on targetSdk 35.

Why not write the cache during normal streaming? Playback uses a **read-only** `CacheDataSource` (`setCacheWriteDataSinkFactory(null)`). Only `DownloadManager` writes. Streaming a song does not silently fill the offline cache.

## Code to focus on

1. [OfflinePlaybackPolicy.kt](../../core/model/src/main/java/com/anhtuta/lilimusic/core/model/OfflinePlaybackPolicy.kt) — playable iff online or `COMPLETED`; offline queue is the visible list filtered to completed paths.
2. [DatabaseMigrations.kt](../../core/database/src/main/java/com/anhtuta/lilimusic/core/database/DatabaseMigrations.kt) — `MIGRATION_2_3` creates `downloads`; songs/lyrics rows stay.
3. [DefaultDownloadRepository.kt](../../core/data/src/main/java/com/anhtuta/lilimusic/core/data/repository/DefaultDownloadRepository.kt) — enqueue audio, cache lyric + cover, follow engine snapshots.
4. [FileCoverStore.kt](../../core/data/src/main/java/com/anhtuta/lilimusic/core/data/download/FileCoverStore.kt) — cover files keyed by song-path hash.
5. [DownloadInfrastructure.kt](../../core/playback/src/main/java/com/anhtuta/lilimusic/core/playback/download/DownloadInfrastructure.kt) / [PlaybackCachePolicy.kt](../../core/playback/src/main/java/com/anhtuta/lilimusic/core/playback/download/PlaybackCachePolicy.kt) — shared `SimpleCache` + read-only playback factory.
6. [LiliDownloadService.kt](../../core/playback/src/main/java/com/anhtuta/lilimusic/core/playback/download/LiliDownloadService.kt) — `dataSync` FGS + progress notification.
7. [ExoPlayerFactory.kt](../../core/playback/src/main/java/com/anhtuta/lilimusic/core/playback/ExoPlayerFactory.kt) — `DefaultMediaSourceFactory(playbackDataSourceFactory)`.
8. [RecordListenWorkRequests.kt](../../core/playback/src/main/java/com/anhtuta/lilimusic/core/playback/RecordListenWorkRequests.kt) — still 60s delay; extra `NetworkType.CONNECTED`.
9. [DownloadsScreen.kt](../../feature/downloads/src/main/java/com/anhtuta/lilimusic/feature/downloads/DownloadsScreen.kt) / [LibraryViewModel.kt](../../feature/library/src/main/java/com/anhtuta/lilimusic/feature/library/LibraryViewModel.kt) — UI + offline tap guard.

Offline policy, from `OfflinePlaybackPolicy.kt`:

```kotlin
fun isPlayable(
    isOnline: Boolean,
    status: DownloadStatus,
): Boolean = isOnline || status == DownloadStatus.COMPLETED

fun playQueue(
    songs: List<Song>,
    isOnline: Boolean,
    completedPaths: Set<String>,
): List<Song> = if (isOnline) songs else songs.filter { it.path in completedPaths }
```

Online, `playQueue` is the visible library list (web `BaiHat.js`). Offline it keeps only completed downloads so skip/next cannot land on a stream-only row.

Listen PUT extra, from `RecordListenWorkRequests.kt`:

```kotlin
fun constraints(): Constraints =
    Constraints
        .Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
```

The 60s-from-start rule in `ListenCountPolicy` did not change. The constraint is the Android extra so a listen counted while offline is not lost; WorkManager sends `PUT /api/song/listens` when the device is back online. Failure still `retry()`.
