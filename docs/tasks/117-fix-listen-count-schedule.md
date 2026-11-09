# 117 — Fix listen count after 60s

## Goal

After playing a song from the start for 60s, the app enqueues WorkManager work that calls `PUT /api/song/listens?path=`.

## Approach

ExoPlayer often reports `positionMs > 0` on the first playing frame, so strict `positionMs == 0L` in `ListenCountPolicy` never schedules. Allow a small start tolerance when **path changes** (new track) while keeping web parity for mid-track resume (no schedule). Add regression tests in `ListenCountPolicyTest` and `FakePlayerControllerTest`.

## Files

- `core/playback/ListenCountPolicy.kt`
- Tests only unless scheduler needs a guard

## Tests

Policy: new song at 50ms schedules; resume at 30s does not; resume at 0 still schedules.

## Done when

Root cause documented in task/learning, tests pass, `./gradlew check`.
