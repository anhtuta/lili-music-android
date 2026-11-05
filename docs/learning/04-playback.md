# PR #15 — Phase 3: playback

[Learning guide index](README.md) · [PR #15](https://github.com/anhtuta/lili-music-android/pull/15)

**What was built.** `MediaSessionService` + ExoPlayer, `PlayerController` (+ Fake), sequence/shuffle-current-first/repeat-one, speed ±0.1, WorkManager listen 60s after play-from-start, DataStore restore (prepared paused), full player + mini-player, library tap plays visible queue, `bai-hat/{path}`. Task [006](../tasks/006-playback.md). Now on main.

**Status:** Merged.

> [!WARNING]
> **Listen counting.** Scheduled 60s after play starts near position 0; cancelled on pause/stop/song change. Not accumulated play time. Matches [docs/PLAN.md](../PLAN.md) and the web timer.

## New terms

| Term | Meaning | Java / web analogy |
| --- | --- | --- |
| Media3 / ExoPlayer | Streams the mp3 | HTML5 Audio, system-integrated |
| MediaSessionService | Foreground service + notification | Background audio worker |
| MediaController | Client talking to the session | Remote control for the engine |
| PlayerController | App port over MediaController | Facade so UI skips ExoPlayer |
| FakePlayerController | In-memory PlayerController for tests | Jest mock of an audio SDK |
| WorkManager | Deferred listen PUT after 60s | Job queue with retry |
| PlayQueue | Shuffle-current-first queue logic | Playlist reducer |
| `FOREGROUND_SERVICE_MEDIA_PLAYBACK` | Manifest permission for music FGS | Required OS background-audio grant |
| `POST_NOTIFICATIONS` | Android 13+ notification permission | Web notification permission |

`FOREGROUND_SERVICE_MEDIA_PLAYBACK` is declared on the playback service in [core/playback/src/main/AndroidManifest.xml](../../core/playback/src/main/AndroidManifest.xml), not the `:app` manifest. `POST_NOTIFICATIONS` is on both.

## Code to focus on

1. [PlayMode.kt](../../core/model/src/main/java/com/anhtuta/lilimusic/core/model/PlayMode.kt) / [PlayQueue.kt](../../core/model/src/main/java/com/anhtuta/lilimusic/core/model/PlayQueue.kt) — Fisher-Yates then move current to index 0.
2. [PlayerController.kt](../../core/playback/src/main/java/com/anhtuta/lilimusic/core/playback/PlayerController.kt) — play/prepare/skip/seek/mode/speed + StateFlow.
3. [FakePlayerController.kt](../../core/playback/src/main/java/com/anhtuta/lilimusic/core/playback/FakePlayerController.kt) — same interface, no Android; for ViewModel tests.
4. [LiliPlaybackService.kt](../../core/playback/src/main/java/com/anhtuta/lilimusic/core/playback/LiliPlaybackService.kt) — `MediaSessionService` wrapping ExoPlayer.
5. [Media3PlayerController.kt](../../core/playback/src/main/java/com/anhtuta/lilimusic/core/playback/Media3PlayerController.kt) — production controller + prefs + listen scheduler.
6. [ListenCountPolicy.kt](../../core/playback/src/main/java/com/anhtuta/lilimusic/core/playback/ListenCountPolicy.kt) — pure schedule-vs-cancel decision.
7. [PlayerViewModel.kt](../../feature/player/src/main/java/com/anhtuta/lilimusic/feature/player/PlayerViewModel.kt) — `restoreLastPlayed` prepares paused (no autoplay).
8. [PlayerScreen.kt](../../feature/player/src/main/java/com/anhtuta/lilimusic/feature/player/PlayerScreen.kt) / [MiniPlayerBar.kt](../../feature/player/src/main/java/com/anhtuta/lilimusic/feature/player/MiniPlayerBar.kt) — Compose chrome.

`FakePlayerController`: real Media3 needs a device, a service, and network audio. The fake keeps `PlaybackState` in memory so `PlayerViewModel` tests stay fast JVM unit tests on CI.

Shuffle-current-first, from `PlayQueue.kt`:

```kotlin
fun shuffleCurrentFirst(
    songs: List<Song>,
    currentPath: String,
    random: Random = Random.Default,
): List<Song> {
    if (songs.size <= 1) return songs
    val shuffled = songs.toMutableList()
    for (index in shuffled.lastIndex downTo 1) {
        val swapWith = random.nextInt(index + 1)
        val current = shuffled[index]
        shuffled[index] = shuffled[swapWith]
        shuffled[swapWith] = current
    }
    val currentIndex = indexOfPath(shuffled, currentPath)
    if (currentIndex > 0) {
        val current = shuffled.removeAt(currentIndex)
        shuffled.add(0, current)
    }
    return shuffled
}
```

Listen policy, from `ListenCountPolicy.kt`:

```kotlin
val startedFromStart =
    currentPlaying &&
        !currentPath.isNullOrBlank() &&
        positionMs <= FROM_START_MAX_POSITION_MS &&
        (!previousPlaying || pathChanged)
val cancelPending = (pathChanged && previousPath != null) || stopped
```

Schedule when playing, path present, `positionMs` at most 1000ms, and (was not playing or path changed). Cancel when path changes or playback stops.

Restore last song paused, from `PlayerViewModel.kt`:

```kotlin
playerController.setPlayMode(prefs.playMode)
playerController.setSpeed(prefs.speed)
playerController.prepare(songs, path)
```
