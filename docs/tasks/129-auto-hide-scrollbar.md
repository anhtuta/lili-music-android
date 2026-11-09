# 129 — Auto-hide compact library scrollbar

## Goal

Keep the library scrollbar compact so it does not consume unnecessary song-list space, and show it only during active scrolling.

## Approach

Reduce the shared scrollbar track and thumb to 12.dp and 10.dp. Observe `LazyListState.isScrollInProgress`, render the existing half-pill thumb while scrolling, and hide it after a short idle delay. Preserve the existing drag mapping and song-row overflow actions.

## Files

- `feature/library/src/main/java/com/anhtuta/lilimusic/feature/library/LibraryChromeInsets.kt`
- `feature/library/src/main/java/com/anhtuta/lilimusic/feature/library/LibraryLazyScrollbar.kt`
- `feature/library/src/test/java/com/anhtuta/lilimusic/feature/library/LibraryScrollbarTest.kt`
- `docs/PLAN.md`

## Tests

Update the Compose scrollbar test to verify the thumb is hidden at rest and shown during a scroll gesture, with a compact width assertion.

## Done when

- The scrollbar reserves only the compact trailing space.
- The thumb is hidden when the list is idle.
- The thumb appears while the user scrolls and auto-hides after scrolling stops.
- Existing drag behavior and song-row three-dot actions remain unchanged.
- Tests and `./gradlew check` pass.
