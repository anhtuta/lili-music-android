# 139 — Keep screen on while synced lyrics play (10 min cap)

## Goal

On Now Playing, when the user enables a settings toggle, the screen stays awake while the current song has a lyric file, synced lyrics are loaded, and playback is playing. A **10 minute** cap applies per **Now Playing visit** (timer keeps across skip next/prev; resets when you leave the route). Settings shows the toggle with a short note about the cap.

## Approach

- `PlayerSettings.keepScreenOnWhileLyrics` in DataStore (default off).
- `NowPlayingKeepScreenOnPolicy` (JVM): eligible = toggle && `song.lyric != null` && `LyricsUiState.frame != null` && `isPlaying`; cap `10 * 60_000` ms from first eligible moment on the visit; **no reset on song change**; leaving Now Playing clears the visit timer.
- `Modifier.keepScreenOn()` on `PlayerScreen` when policy says on.
- Wire in `:app` `NowPlayingEntry`: combine `PlayerViewModel` + `LyricsViewModel` (same nav back stack).
- Settings: switch + supporting text for 10 min cap.

Non-goals: wake lock permission, expanded-lyrics-only scope, mini player.

## Files

- `core:model` — `PlayerSettings`
- `core:datastore` — preference key, parser
- `core:data` — `SettingsRepository`
- `feature:settings` — UI, ViewModel, strings, tests
- `feature:player` — policy, `rememberNowPlayingKeepScreenOn`, `PlayerScreen` / `PlayerRoute`
- `app` — `NowPlayingEntry` wiring

## Tests

- JVM: `NowPlayingKeepScreenOnPolicyTest`
- Settings Compose: toggle + cap note visible
- DataStore parser round-trip for new key

## Done when

- Toggle persists; behavior matches eligibility + 10 min cap.
- Tests pass; `./gradlew check` (or affected modules if detekt JDK blocked).
