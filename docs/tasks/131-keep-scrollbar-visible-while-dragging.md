# 131 — Keep scrollbar visible while dragging

## Goal

Prevent the library scrollbar from disappearing while the user is actively dragging it.

## Approach

Track scrollbar pointer-drag activity separately from `LazyListState.isScrollInProgress`. Keep the scrollbar visible on drag start and throughout drag updates, clear that interaction state on drag end/cancel, and let the existing idle delay hide the scrollbar only after both the gesture and list scrolling have stopped.

## Files

- `feature/library/src/main/java/com/anhtuta/lilimusic/feature/library/LibraryLazyScrollbar.kt`
- `feature/library/src/test/java/com/anhtuta/lilimusic/feature/library/LibraryScrollbarTest.kt`
- `docs/PLAN.md`

## Tests

Extend the library Compose scrollbar test to cover visibility during the scrollbar interaction and hiding after the interaction/list becomes idle.

## Done when

- The scrollbar remains visible for the complete scrollbar drag.
- A completed drag still hides the scrollbar after the idle delay.
- Existing touch-to-jump, end-jump, and stable-thumb behavior remains unchanged.
- Tests and `./gradlew check` pass.
