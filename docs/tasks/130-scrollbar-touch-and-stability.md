# 130 — Responsive and stable library scrollbar

## Goal

Make the compact library scrollbar easier to use, allow touching any point on its rail to jump there, and prevent the thumb from changing height while scrolling.

## Approach

Use the full trailing rail as the pointer target instead of only the narrow thumb. Taps and drags map their vertical position directly to the catalog index, so touching the bottom jumps to the end. Use a fixed thumb fraction and explicit start/end clamping so the thumb geometry stays stable and reaches both list boundaries.

## Files

- `feature/library/src/main/java/com/anhtuta/lilimusic/feature/library/LibraryLazyScrollbar.kt`
- `feature/library/src/main/java/com/anhtuta/lilimusic/feature/library/LazyListScrollbarMath.kt`
- `feature/library/src/test/java/com/anhtuta/lilimusic/feature/library/LazyListScrollbarMathTest.kt`
- `feature/library/src/test/java/com/anhtuta/lilimusic/feature/library/LibraryScrollbarTest.kt`
- `docs/PLAN.md`

## Tests

Add math coverage for stable thumb height and exact top/bottom positions. Extend the Compose test to cover the full scrollbar rail as the interaction target and compact thumb dimensions.

## Done when

- Users can touch or drag anywhere in the scrollbar rail.
- Touching the bottom jumps the list to its final item.
- Thumb height remains stable across scroll positions.
- Thumb reaches the top and bottom boundaries correctly.
- Existing auto-hide behavior and song-row overflow actions remain unchanged.
- Tests and `./gradlew check` pass.
