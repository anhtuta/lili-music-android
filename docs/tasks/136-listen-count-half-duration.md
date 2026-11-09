# 136 — Listen count at 50% played time (Android)

## Goal

Count a listen when the user has played **at least half of the track length** on the same `path`, regardless of start position. Example: 4:00 song → 2:00 of playback; 1:00 song → 0:30. Mid-track resume counts toward the same threshold. **Android-only**; web still uses 60s from position 0.

## Approach

Replace the web-parity `ListenCountPolicy` (60s delay from start) with session state that accumulates **media position advancement** while `isPlaying` (ignores large seeks). When accumulated ≥ `durationMs / 2`, enqueue `RecordListenWorker` with `PUT /api/song/listens?path=`. Pause cancels pending WorkManager delay but keeps accumulated progress; path change cancels and resets. Unknown duration uses `PlaybackDuration.UNKNOWN_FALLBACK_MS` until Media3 reports a length. One listen per play session on a path until the path changes or the queue restarts (`play` / `prepare`).

## Files

- `core/playback/ListenCountPolicy.kt`
- `core/playback/ListenCountSession.kt` (session state + update)
- `core/playback/Media3PlayerController.kt`
- `core/playback/FakePlayerController.kt`
- `core/playback/RecordListenWorkRequests.kt`
- `ListenCountDecision` / `ListenCountScheduler`
- `docs/PLAN.md` (playback + API notes)

## Tests

- `ListenCountPolicyTest` / session tests: threshold math, mid-track resume, pause/resume preserves progress, path change cancels, queue restart, seek jump not counted
- `FakePlayerControllerTest`: half-duration schedules listen
- `RecordListenWorkRequestsTest`: dynamic delay
- `WorkListenCountSchedulerTest`: unchanged behaviour with `delayMs`

## Done when

PLAN updated, tests pass, `./gradlew check`.
