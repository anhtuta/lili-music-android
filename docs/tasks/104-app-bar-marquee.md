# 104 — Auto-scrolling title and artist

## Goal

When the Now Playing app-bar title or artist overflows, it marquees slowly: left-aligned pause, scroll until the end is visible, pause ~1.5–2s, jump back, repeat. Text that fits does not move. The user can drag to scroll; auto-scroll resumes after a short pause on release. Title and artist scroll independently; a new song restarts both.

## Approach

Do **not** use `Modifier.basicMarquee` (no manual drag). Each line is `horizontalScroll(ScrollState)` + `LaunchedEffect` that `animateScrollTo(maxValue, tween(linear, duration from ~30–40 dp/s))`, pauses, then `scrollTo(0)`. Pause the loop while `collectIsDraggedAsState()` on the scroll `interactionSource` (not `isScrollInProgress`, which is also true during our animation). Constrain the viewport with `fillMaxWidth()`; `softWrap = false`. Restart when `text` changes.

Non-goals: marquee elsewhere (mini player, library); vertical scroll.

## Files

- `core/ui/.../MarqueeText.kt` (or `feature/player` if kept player-only)
- `feature/player/.../PlayerScreen.kt` (`NowPlayingAppBarTitle`)
- tests

## Tests

- Short text: `ScrollState.value` stays 0 after advancing the test clock.
- Long text in a narrow box: value increases over time (`mainClock` + linear tween).
- Manual swipe moves the scroll; overflow-only (maxValue > 0 iff text is wider than the viewport).

## Done when

- Overflowing title/artist marquee independently; fitting text is static; drag then resume works; song change resets.
- Tests pass; `./gradlew check`.
