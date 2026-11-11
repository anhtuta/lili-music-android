# 25 — Keep screen on while synced lyrics (task 139)

## What changed

Settings includes **Keep screen on while lyrics play** (default off). On Now Playing, when the toggle is on, the song has a lyric file, synced lyrics are loaded (`LyricSyncFrame` present), and playback is **playing**, the window sets `FLAG_KEEP_SCREEN_ON` via `view.keepScreenOn`. Each song gets up to **10 minutes** of keep-on while eligible (`song.path` change, pause, or leaving the route starts a fresh window).

## Terms

| Android | Idea | Web analog |
| --- | --- | --- |
| `view.keepScreenOn` | Window flag; screen stays at user brightness | No direct equivalent on desktop |
| `KeepScreenOnEffect` | Compose `DisposableEffect` toggles the flag | — |
| `NowPlayingKeepScreenOnPolicy` | Pure eligibility + 10 min cap | — |

## Files to read

1. [NowPlayingEntry.kt](../../app/src/main/java/com/anhtuta/lilimusic/NowPlayingEntry.kt) — merges player + lyrics state.
2. [NowPlayingKeepScreenOnPolicy.kt](../../feature/player/src/main/java/com/anhtuta/lilimusic/feature/player/NowPlayingKeepScreenOnPolicy.kt)
3. [SettingsScreen.kt](../../feature/settings/src/main/java/com/anhtuta/lilimusic/feature/settings/SettingsScreen.kt) — switch + 10 min note.
