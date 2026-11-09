# 135 — Animate player controls on lyric expand

## Goal

Expanding lyrics via the drag handle (tap or slow pull) fades and slides the transport chrome away instead of removing it instantly.

## Approach

Drive a 0–1 expansion progress with `Animatable`: `tween` on tap, `snapTo` while dragging via `LyricPaneExpansion.dragProgress`. Apply `graphicsLayer` alpha and `translationY` on the controls block; remove from composition only after progress reaches 1 and the animation is idle.

## Files

- `feature/player/.../LyricPaneExpansion.kt`
- `feature/player/.../LyricChromeExpansionAnimation.kt`
- `feature/player/.../PlayerLyricHandle.kt`
- `feature/player/.../PlayerScreen.kt` (`PlayerChromeFooter`)
- Tests: `LyricPaneExpansionTest`, `PlayerScreenPolishTest`

## Tests

Unit coverage for `dragProgress`. Compose polish test advances the main clock after expand/restore so controls visibility matches the animated end state.

## Done when

- Tap and drag on the lyric handle animate chrome hide/show.
- Tests pass; `./gradlew check`.
