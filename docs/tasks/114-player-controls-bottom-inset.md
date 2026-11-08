# 114 — Bottom inset for Now Playing controls

## Goal

Add comfortable bottom spacing so the seek bar and transport row are not flush with the phone edge (safe area + margin).

## Approach

Apply `navigationBarsPadding()` plus a small fixed bottom padding on the controls column in compact (and expanded if needed) player layout.

## Files

- `feature/player/PlayerScreen.kt` and/or `PlayerControls.kt`
- `PlayerScreenTest` or layout test asserting extra bottom inset

## Tests

Compose layout: controls bottom ≥ navigation bar padding (or min dp).

## Done when

Insets visible on compact phone, tests pass, `./gradlew check`.
