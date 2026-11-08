# 123 — Remove Now Playing pull-down dismiss

## Goal

Pulling down on Now Playing no longer navigates back to Library; back is only via the app bar control.

## Approach

Remove `nestedScroll(rememberNowPlayingDismissScroll)` from `PlayerScreen`. Delete unused dismiss scroll composable if nothing references it; keep `NowPlayingDismiss` math only if tests remain valuable, else remove with tests.

## Files

- `feature/player/PlayerScreen.kt`
- `feature/player/NowPlayingDismiss.kt` (trim or delete)

## Tests

Existing `NowPlayingDismissTest` may be removed with dead code, or kept for pure threshold math if object stays.

## Done when

No nested-scroll dismiss on player, tests pass, `./gradlew check` (or player module).
