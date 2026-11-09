# 23 — Listen count at 50% duration (Android)

## What was built

Task **136** replaces the web-parity rule (60 seconds from position 0) with an **Android-only** threshold: when **≥ 50%** of the track duration has been played on the same `path`, the app enqueues `RecordListenWorker` → `PUT /api/song/listens?path=`. Start position does not matter—resuming mid-song counts playback time toward the half. Pause cancels the pending WorkManager delay but keeps accumulated progress. Changing `path` or restarting the queue (`play` / `prepare`) resets the session.

## Terms

| Kotlin / Android | Meaning | Web / Java analogy |
| --- | --- | --- |
| `ListenCountSession` | Per-controller state: accumulated play ms + whether PUT already scheduled | Client-side session object |
| `ListenCountAccumulation` | Adds only small forward position deltas (ignores seeks) | Ignoring `seeked` events in a progress listener |
| `ListenCountDecision.delayMs` | WorkManager delay until PUT when still below threshold | `setTimeout` with dynamic ms |
| `requiredListeningMs` | `durationMs / 2` (uses 10 min fallback when length unknown) | Half of `audio.duration` |

## Code to focus on

1. [ListenCountSession.kt](../../core/playback/src/main/java/com/anhtuta/lilimusic/core/playback/ListenCountPolicy.kt) — session updates and schedule/cancel decisions.
2. [Media3PlayerController.kt](../../core/playback/src/main/java/com/anhtuta/lilimusic/core/playback/Media3PlayerController.kt) — 30ms ticker adds progress; `queueRestartPending` on `applyQueue`.
3. [RecordListenWorkRequests.kt](../../core/playback/src/main/java/com/anhtuta/lilimusic/core/playback/RecordListenWorkRequests.kt) — dynamic `delayMs`.
4. [ListenCountSessionTest](../../core/playback/src/test/java/com/anhtuta/lilimusic/core/playback/ListenCountPolicyTest.kt) — mid-track resume, pause/resume, ticks past half.

## Web vs Android

The Lumen backend is unchanged. The **web player** still counts after 60s from `{ currentTime === 0 }`. **This app** intentionally diverges; see [PLAN.md](../PLAN.md) API table and playback section.
